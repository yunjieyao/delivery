package com.app.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

@SuppressLint("DefaultLocale")
public class FileUtil {

	// 检查创建目录
	public static boolean checkAndCreateDirs(String path) {
		File file = new File(path);
		if (!file.exists()) {
			int y = path.lastIndexOf('.');
			int x = path.lastIndexOf('/');
			if (y > 0 && y > x) {
				String parentDir = path.substring(0, x);
				return new File(parentDir).mkdirs();
			} else {
				return file.mkdirs();
			}
		}
		return true;
	}

	// 读取文件字节
	public static byte[] readFileContent(String filepath) throws IOException {
		BufferedInputStream i = null;
		BufferedOutputStream o = null;
		try {
			i = new BufferedInputStream(new FileInputStream(filepath));
			ByteArrayOutputStream ao = new ByteArrayOutputStream();
			o = new BufferedOutputStream(ao);

			int read = -1;
			byte[] buffer = new byte[2048];
			while (true) {
				read = i.read(buffer);
				if (read == -1) {
					// 读完了
					break;
				}
				o.write(buffer, 0, read);
			}
			o.flush();

			return ao.toByteArray();
		} finally {
			if (i != null) {
				try {
					i.close();
				} catch (Exception e) {
				}
			}
			if (o != null) {
				try {
					o.close();
				} catch (Exception e) {
				}
			}
		}
	}

	// 保存流到文件
	public static boolean saveStreamFile(InputStream is, String filepath) throws IOException {
		boolean result = true;
		BufferedOutputStream o = null;
		try {
			FileOutputStream fileOutputStream = new FileOutputStream(filepath);
			o = new BufferedOutputStream(fileOutputStream);
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = is.read(b)) != -1) {
				o.write(b, 0, len);
			}
			o.flush();
		} finally {
			if (o != null) {
				try {
					o.close();
				} catch (Exception e) {
				}
			}
		}
		return result;
	}

	// 保存字节到文件
	public static void saveFileContent(String filepath, byte[] content) throws IOException {
		BufferedOutputStream o = null;
		try {
			o = new BufferedOutputStream(new FileOutputStream(filepath));
			o.write(content);
		} finally {
			if (o != null) {
				try {
					o.flush();
					o.close();
				} catch (Exception e) {
				}
			}
		}
	}
	
	public static File createFile(String folderPath, String fileName) {
		File destDir = new File(folderPath);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}
		return new File(folderPath, fileName);
	}

	// 删除一个目录（级联删除）
	public static boolean deleteDir(String path) {
		boolean success = cleanDir(path);
		if (success) {
			success = new File(path).delete();
		}
		return success;
	}

	// 删除一个文件
	public static boolean deleteFile(String path) {
		boolean success = false;
		File file = new File(path);
		if (file.exists()) {
			success = file.delete();
		}
		return success;
	}

	// 清除一个目录的内容
	public static boolean cleanDir(String path) {
		File file = new File(path);
		boolean success = true;
		if (file.exists()) {
			File[] list = file.listFiles();
			if (list != null) {
				for (File tempFile : list) {
					if (tempFile.isDirectory()) {
						boolean ret = deleteDir(tempFile.getPath());
						if (!ret) {
							success = false;
						}
					} else {
						boolean ret = tempFile.delete();
						if (!ret) {
							success = false;
						}
					}
				}
			}
		}
		return success;
	}

	// 目录copy，路径必须以"/"结束，否则会有问题。
	public static boolean copyFolder(String sourceDir, String targetDir) {
		if (!sourceDir.endsWith(File.separator)) {
			sourceDir = sourceDir + File.separator;
		}
		if (!targetDir.endsWith(File.separator)) {
			targetDir = targetDir + File.separator;
		}

		try {
			(new File(targetDir)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File a = new File(sourceDir);
			String[] SubFilenameList = a.list();
			File temp = null;
			for (String filename : SubFilenameList) {
				temp = new File(sourceDir + filename);

				if (temp.isFile()) {
					copyFile(sourceDir + filename, targetDir + filename);
				}
				if (temp.isDirectory()) {// 如果是子文件夹
					copyFolder(sourceDir + filename + File.separator, targetDir + filename + File.separator);
				}
			}
		} catch (Exception e) {
			Log.d("FileUtil", "copy folder failed!", e);
			return false;

		}
		return true;
	}

	// 文件复制 添加文件锁，多线程写文件导致文件损坏和缺失
	public static void copyFile(String source, String target) throws IOException {
		FileInputStream i = null;
		FileOutputStream o = null;
		try {
			i = new FileInputStream(source);
			o = new FileOutputStream(target);
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = i.read(b)) != -1) {
				o.write(b, 0, len);
			}
		} finally {
			if (i != null) {
				try {
					i.close();
				} catch (Exception e) {
				}
			}
			if (o != null) {
				try {
					o.flush();
					o.close();
				} catch (Exception e) {
				}
			}
		}
	}

	// 文件移动
	public static void moveFile(String source, String target) throws IOException {
		copyFile(source, target); // copy文件
		deleteFile(source); // 删除原始文件
	}

	// 文件复制
	public static void copyAssetFile(Context context, String source, String target) throws IOException {
		InputStream i = null;
		FileOutputStream o = null;
		try {
			i = context.getAssets().open(source);
			o = new FileOutputStream(target);
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = i.read(b)) != -1) {
				o.write(b, 0, len);
			}
			o.flush();
		} finally {
			if (i != null) {
				try {
					i.close();
				} catch (Exception e) {
				}
			}
			if (o != null) {
				try {
					o.close();
				} catch (Exception e) {
				}
			}
		}
	}

	public static void closeInputStream(InputStream in) {
		if (in != null) {
			try {
				in.close();
			} catch (Exception e) {

			}
		}
	}

	public static void closeOutputStream(OutputStream out) {
		if (out != null) {
			try {
				out.close();
			} catch (Exception e) {

			}
		}
	}

	public static String getFilenameFromUrl(String url) {
		String r = url;
		int p = url.lastIndexOf("/");
		if (p != -1) {
			r = url.substring(p + 1);
		}
		p = r.indexOf("?");
		if (p != -1) {
			r = r.substring(0, p);
		}
		return r;
	}

	public static String getFileExtFromUrl(String url) {
		String r = getFilenameFromUrl(url);
		return getFileExtFromFilename(r);
	}

	public static String getFileExtFromFilename(String filename) {
		int p = filename.indexOf(".");
		String ext = "";
		if (p != -1) {
			ext = filename.substring(p + 1);
		}
		return ext;
	}

	public static String getFilenameFromContentDisposition(String contentDisposition) {
		if (contentDisposition != null && !contentDisposition.equals("")) {
			String[] values = contentDisposition.split(";");
			for (String v : values) {
				if (v != null && v.trim().toLowerCase().startsWith("filename=")) {
					try {
						int start = v.indexOf("\"");
						int end = v.lastIndexOf("\"");
						String filename = v.substring(start + 1, end);
						return filename;
					} catch (Exception e) {
						e.printStackTrace(System.out);
						return null;
					}
				}
			}
		}
		return null;
	}

}
