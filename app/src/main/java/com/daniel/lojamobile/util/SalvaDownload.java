package com.daniel.lojamobile.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;

public class SalvaDownload {
    public static File writeResponseBodyToDisk(ResponseBody body, String nome) {
        try {

            File arquivo = null;
            arquivo = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + nome);
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(arquivo);
                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                }

                outputStream.flush();
                //abre arquivo

                return arquivo;
            } catch (IOException e) {
                return null;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return  null;
        }
    }

    public static void salvar(ResponseBody body, String nome) {
        String PATH = Environment.getExternalStorageDirectory().getAbsolutePath();

        try {
            String FilePath = PATH + "/" + nome;

            File file = new File(PATH);
            file.mkdirs();

            OutputStream output = new FileOutputStream(FilePath);
            output.flush();
            output.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
