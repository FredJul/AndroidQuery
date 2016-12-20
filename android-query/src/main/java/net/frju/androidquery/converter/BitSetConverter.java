package net.frju.androidquery.converter;

import android.os.Build;
import android.support.annotation.RequiresApi;

import net.frju.androidquery.annotation.BaseTypeConverter;

import java.util.BitSet;

public class BitSetConverter extends BaseTypeConverter<byte[], BitSet> {

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public byte[] convertToDb(BitSet model) {
        return model == null ? null : model.toByteArray();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public BitSet convertFromDb(byte[] data) {
        return data == null ? null : BitSet.valueOf(data);
    }
}
