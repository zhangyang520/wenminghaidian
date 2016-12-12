package com.zhjy.hdcivilization.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @项目名：AroundYou
 * @类名称：ZipUtil
 * @类描述： 解压缩类（在导包的时候，应添加ant.jar解压缩包）
 * @创建人：HXF
 * @修改人：
 * @创建时间：2015-9-15 上午9:44:42
 */
public class ZipUtil {

    private static ZipUtil instance;

    private ZipUtil() {

    }

    public static ZipUtil getInstance() {
        if (instance == null) {
            synchronized (ZipUtil.class) {
                if (instance == null) {
                    instance = new ZipUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 解压缩zip包
     *
     * @param zipFilePath zip文件路径
     * @param targetPath  解压缩到的位置，如果为null或空字符串则默认解压缩到跟zip包同目录跟zip包同名的文件夹下
     * @throws IOException
     * @author niyl
     */
    public void unZip(String zipFilePath, String targetPath)
            throws IOException {
        OutputStream os = null;
        InputStream is = null;
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(zipFilePath);
            String directoryPath = "";
            if (null == targetPath || "".equals(targetPath)) {
                directoryPath = zipFilePath.substring(0, zipFilePath
                        .lastIndexOf("."));
            } else {
                directoryPath = targetPath;
            }
            @SuppressWarnings("rawtypes")
            Enumeration entryEnum = zipFile.entries();
            if (null != entryEnum) {
                ZipEntry zipEntry = null;
                while (entryEnum.hasMoreElements()) {
                    zipEntry = (ZipEntry) entryEnum.nextElement();
                    if (zipEntry.getSize() > 0) {
                        // 文件
                        File targetFile = FileUtil.getInstance().buildFile(directoryPath
                                + File.separator + zipEntry.getName(), false);
                        os = new BufferedOutputStream(new FileOutputStream(targetFile));
                        is = zipFile.getInputStream(zipEntry);
                        byte[] buffer = new byte[4096];
                        int readLen = 0;
                        while ((readLen = is.read(buffer, 0, 4096)) >= 0) {
                            os.write(buffer, 0, readLen);
                        }

                        os.flush();
                        os.close();
                    }
                    if (zipEntry.isDirectory()) {
                        String pathTemp = directoryPath + File.separator
                                + zipEntry.getName();
                        File file = new File(pathTemp);
                        file.mkdirs();
                        System.out.println(pathTemp);
//	                        continue;
                    }
                }
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (null != zipFile) {
                zipFile = null;
            }
            if (null != is) {
                is.close();
            }
            if (null != os) {
                os.close();
            }
        }
    }

    public static class FileUtil {

        public static FileUtil instance;


        public FileUtil() {

        }

        public static FileUtil getInstance() {
            if (instance == null) {
                synchronized (FileUtil.class) {
                    if (instance == null) {
                        instance = new FileUtil();
                    }
                }
            }
            return instance;
        }


        public File buildFile(String fileName, boolean isDirectory) {
            File target = new File(fileName);
            if (isDirectory) {
                target.mkdirs();
            } else {
                if (!target.getParentFile().exists()) {
                    target.getParentFile().mkdirs();
                    target = new File(target.getAbsolutePath());
                }
            }
            return target;
        }
    }


    public int streamLen;

    /**
     * @描述:把raw文件夹下压缩包存储到SD卡中
     * @方法名：&unZip
     * @创建时间：&2015-9-16 &上午10:10:43
     */
    public void downZip(InputStream inputStream, String filePath) {
        int readCount = 0;
        FileOutputStream output;
        try {
            output = new FileOutputStream(filePath);
            BufferedInputStream b = new BufferedInputStream(inputStream);
            int len = 2048;
            byte[] buffer = new byte[len];
            while ((readCount = b.read(buffer)) != -1) {
                output.write(buffer, 0, readCount);
            }
            output.flush();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("e.getMessage==" + e.getMessage());
        }
    }
}