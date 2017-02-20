package ${package_name};

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import net.frju.androidquery.database.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Q {

    private static DefaultResolver sResolver;

    public static void init(@NonNull Context context) {
        if (sResolver == null) {
            sResolver = new DefaultResolver();
            sResolver.init(context);
        }
    }

    public static @NonNull DefaultResolver getResolver() {
        return sResolver;
    }

    public static class DefaultResolver implements Resolver {

        private static HashMap<Class<?>, DatabaseProvider> mProviders = new HashMap<>();

        public void init(@NonNull Context context) {
            HashMap<String, DatabaseProvider> providersByName = new HashMap<>();

            <#list providers as provider>
            providersByName.put("${provider.toString()}", new ${provider.toString()}(context.getApplicationContext()));
            </#list>

            <#list tables as table>
            <#if table.getDatabaseProvider().toString() != "java.lang.Void">
                    mProviders.put(${table.getPackage()}.${table.getName()}.class, providersByName.get("${table.getDatabaseProvider().toString()}"));
                    mProviders.put(${formatConstant(table.getName())}.class, providersByName.get("${table.getDatabaseProvider().toString()}")); // to be more error-tolerant
            </#if>
            </#list>
        }

        @Override
        public void initModelWithInitMethods(@NonNull Object model) {
            <#assign isAssignableFrom>
                <#list tables as table>
                else if (model instanceof ${table.getPackage()}.${table.getName()}) {
                    <#list table.getInitMethodNames() as initMethod>
                    ((${table.getPackage()}.${table.getName()})model).${initMethod}();
                    </#list>
                }
                </#list>
            </#assign>

            ${isAssignableFrom?trim?remove_beginning("else ")}
        }

        @Override
        public @NonNull Class<?> getModelClassFromName(@NonNull String modelDbName) {
            switch (modelDbName) {
            <#list tables as table>
            case "${table.getDbName()}":
                return ${table.getPackage()}.${table.getName()}.class;
            </#list>
            default:
                throw new IllegalStateException("The modelDbName " + modelDbName + " is not a correct");
            }
        }

        @Override
        public @NonNull DbModelDescriptor getDbModelDescriptor(@NonNull Class<?> classDef) {
            <#assign isAssignableFrom>
                <#list tables as table>
                } else if (classDef.isAssignableFrom(${table.getPackage()}.${table.getName()}.class)) {
                    return s${table.getName()};
                </#list>
                }
            </#assign>

            ${isAssignableFrom?trim?remove_beginning("} else ")} else {
                throw new IllegalStateException("Please ensure all SQL tables are annotated with @DbModel");
            }
        }

        @Override
        public @NonNull Class<?>[] getModelsForProvider(@Nullable Class<? extends DatabaseProvider> providerClass) {
            ArrayList<Class<?>> result = new ArrayList<>();

            <#list tables as table>
            if (${table.getDatabaseProvider().toString()}.class.equals(providerClass)) {
                result.add(${table.getPackage()}.${table.getName()}.class);
            }
            </#list>

            if (result.size() == 0) {
                throw new IllegalStateException("This provider does not have any @DbModel models registered into that resolver");
            }

            return result.toArray(new Class<?>[result.size()]);
        }

        @Override
        public @Nullable DatabaseProvider getDatabaseProviderForModel(@Nullable Class<?> model) {
            return mProviders.get(model);
        }
    }

    <#list tables as table>
        private static ${formatConstant(table.getName())} s${table.getName()} = new ${formatConstant(table.getName())}();
    </#list>
}