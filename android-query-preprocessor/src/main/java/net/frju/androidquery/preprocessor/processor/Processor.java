package net.frju.androidquery.preprocessor.processor;

import com.google.auto.service.AutoService;
import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.FormatterException;

import net.frju.androidquery.annotation.DbField;
import net.frju.androidquery.annotation.DbModel;
import net.frju.androidquery.annotation.TypeConverter;
import net.frju.androidquery.preprocessor.processor.data.Data;
import net.frju.androidquery.preprocessor.processor.data.parse.ParseAnnotations;
import net.frju.androidquery.preprocessor.processor.data.validator.PrimaryKeyMustBeUnique;
import net.frju.androidquery.preprocessor.processor.data.validator.TableNamesMustBeUniqueValidator;
import net.frju.androidquery.preprocessor.processor.freemarker.DataModel;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@AutoService(javax.annotation.processing.Processor.class)
public class Processor extends AbstractProcessor {
    private static final String GENERATED_FILE_PATH = "Q.java";
    private static final String GENERATED_FILE_NAME = "Q";

    private FreeMarker mFreeMarker;
    private String mGenFilePackage = "net.frju.androidquery.gen";

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        mFreeMarker = getFreeMarker();
        if (env.getOptions().containsKey("generatedFilePackageName")) {
            mGenFilePackage = env.getOptions().get("generatedFilePackageName");
        }
        Context.createInstance(env);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment env) {
        Set<? extends Element> tableElements = env.getElementsAnnotatedWith(DbModel.class);

        if (tableElements != null && tableElements.size() > 0) {

            Set<? extends Element> converterElements = env.getElementsAnnotatedWith(TypeConverter.class);
            Data data = ParseAnnotations.parse(tableElements, converterElements);

            try {
                validate(data);
            } catch (ValidatorException e) {
                Context.getInstance().getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        e.getMessage(),
                        e.getElement()
                );

                return false;
            }

            try {
                String body = mFreeMarker.getMappedFileBodyFromTemplate(GENERATED_FILE_PATH, DataModel.create(mGenFilePackage, data));
                createFile(mGenFilePackage, GENERATED_FILE_NAME, body);
            } catch (IOException | FormatterException e) {
                Context.getInstance().getMessager().printMessage(
                        Diagnostic.Kind.ERROR,
                        e.getMessage()
                );
            }
        }

        return true;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> set = new HashSet<>();
        set.add(DbModel.class.getCanonicalName());
        set.add(DbField.class.getCanonicalName());
        return set;
    }

    private void validate(Data data) throws ValidatorException {
        for (Validator validator : getValidators(data))
            validator.validate();

        if (mFreeMarker == null)
            throw new ValidatorException("FATAL ERROR: Could not create an instance of FreeMarker");
    }

    private Validator[] getValidators(Data data) {
        return new Validator[]{
                new TableNamesMustBeUniqueValidator(data),
                new PrimaryKeyMustBeUnique(data)
        };
    }

    private void createFile(String packageName, String name, String body) throws IOException, FormatterException {
        String nameWithPackage = packageName + "." + name;
        JavaFileObject jfo = Context.getInstance().getFiler().createSourceFile(nameWithPackage);

        Context.getInstance().getMessager().printMessage(
                Diagnostic.Kind.NOTE,
                "creating SQLKing Q.java source file at: " + jfo.toUri());

        String formattedSource = new Formatter().formatSource(body);

        Writer writer = jfo.openWriter();
        writer.append(formattedSource);
        writer.close();

        // Workaround for some AndroidStudio display errors where he is searching for debug folder by default
        if (jfo.getName().contains("/apt/release/")) {
            try {
                String debugFileName = jfo.getName().replace("/apt/release/", "/apt/debug/");
                new File(debugFileName).getParentFile().mkdirs();
                Writer debugFileWriter = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(debugFileName), "utf-8"));
                debugFileWriter.write(formattedSource);
                debugFileWriter.close();
            } catch (Exception e) {
                // ignore it
            }
        }
    }

    private FreeMarker getFreeMarker() {
        try {
            return new FreeMarker();
        } catch (IOException e) {
            return null;
        }
    }
}