package srt.cloud.framework.dbswitch.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;

@Slf4j
public final class TypeConvertUtils {

	private TypeConvertUtils() {
		throw new IllegalStateException("Utility class can not create instance!");
	}

	public static String castToString(final Object in) {
		if (in instanceof Character) {
			return in.toString();
		} else if (in instanceof String) {
			return in.toString();
		} else if (in instanceof Character) {
			return in.toString();
		} else if (in instanceof java.sql.Clob) {
			return clob2Str((java.sql.Clob) in);
		} else if (in instanceof Number) {
			return in.toString();
		} else if (in instanceof java.sql.RowId) {
			return in.toString();
		} else if (in instanceof Boolean) {
			return in.toString();
		} else if (in instanceof java.util.Date) {
			return in.toString();
		} else if (in instanceof java.time.LocalDate) {
			return in.toString();
		} else if (in instanceof java.time.LocalTime) {
			return in.toString();
		} else if (in instanceof java.time.LocalDateTime) {
			return in.toString();
		} else if (in instanceof java.time.OffsetDateTime) {
			return in.toString();
		} else if (in instanceof java.sql.SQLXML) {
			return in.toString();
		} else if (in instanceof java.sql.Array) {
			return in.toString();
		} else if (in instanceof java.util.UUID) {
			return in.toString();
		} else if ("org.postgresql.util.PGobject".equals(in.getClass().getName())) {
			return in.toString();
		} else if ("org.postgresql.jdbc.PgSQLXML".equals(in.getClass().getName())) {
			try {
				Class<?> clz = in.getClass();
				Method getString = clz.getMethod("getString");
				return getString.invoke(in).toString();
			} catch (Exception e) {
				return "";
			}
		} else if (in.getClass().getName().equals("oracle.sql.INTERVALDS")) {
			return in.toString();
		} else if (in.getClass().getName().equals("oracle.sql.INTERVALYM")) {
			return in.toString();
		} else if (in.getClass().getName().equals("oracle.sql.TIMESTAMPLTZ")) {
			return in.toString();
		} else if (in.getClass().getName().equals("oracle.sql.TIMESTAMPTZ")) {
			return in.toString();
		} else if (in.getClass().getName().equals("oracle.sql.BFILE")) {
			Class<?> clz = in.getClass();
			try {
				Method methodFileExists = clz.getMethod("fileExists");
				boolean exists = (boolean) methodFileExists.invoke(in);
				if (!exists) {
					return "";
				}

				Method methodOpenFile = clz.getMethod("openFile");
				methodOpenFile.invoke(in);

				try {
					Method methodCharacterStreamValue = clz.getMethod("getBinaryStream");
					java.io.InputStream is = (java.io.InputStream) methodCharacterStreamValue.invoke(in);

					String line;
					StringBuilder sb = new StringBuilder();

					java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(is));
					while ((line = br.readLine()) != null) {
						sb.append(line);
					}

					return sb.toString();
				} finally {
					Method methodCloseFile = clz.getMethod("closeFile");
					methodCloseFile.invoke(in);
				}
			} catch (java.lang.reflect.InvocationTargetException ex) {
				log.warn("Error for handle oracle.sql.BFILE: ", ex);
				return "";
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		} else if (in.getClass().getName().equals("microsoft.sql.DateTimeOffset")) {
			return in.toString();
		} else if (in instanceof byte[]) {
			return new String((byte[]) in);
		}

		return null;
	}

	public static byte[] castToByteArray(final Object in) {
		if (in instanceof byte[]) {
			return (byte[]) in;
		} else if (in instanceof java.util.Date) {
			return in.toString().getBytes();
		} else if (in instanceof java.sql.Blob) {
			return blob2Bytes((java.sql.Blob) in);
		} else if (in instanceof String || in instanceof Character) {
			return in.toString().getBytes();
		} else if (in instanceof java.sql.Clob) {
			return clob2Str((java.sql.Clob) in).getBytes();
		} else {
			return toByteArray(in);
		}
	}

	public static Object castByDetermine(final Object in) {
		if (null == in) {
			return null;
		}

		if (in instanceof BigInteger) {
			return ((BigInteger) in).longValue();
		} else if (in instanceof BigDecimal) {
			BigDecimal decimal = (BigDecimal) in;
			if (decimal.doubleValue() > 2.147483647E9D || decimal.doubleValue() < -2.147483648E9D) {
				return 0D;
			}
			return decimal.doubleValue();
		} else if (in instanceof java.sql.Clob) {
			return clob2Str((java.sql.Clob) in);
		} else if (in instanceof java.sql.Array
				|| in instanceof java.sql.SQLXML) {
			try {
				return castToString(in);
			} catch (Exception e) {
				log.warn("Unsupported type for convert {} to java.lang.String", in.getClass().getName());
				return null;
			}
		} else if (in instanceof java.sql.Blob) {
			try {
				return blob2Bytes((java.sql.Blob) in);
			} catch (Exception e) {
				log.warn("Unsupported type for convert {} to byte[] ", in.getClass().getName());
				return null;
			}
		} else if (in instanceof java.sql.Struct) {
			log.warn("Unsupported type for convert {} to java.lang.String", in.getClass().getName());
			return null;
		}

		return in;
	}

	public static byte[] blob2Bytes(java.sql.Blob blob) {
		try (java.io.InputStream inputStream = blob.getBinaryStream()) {
			try (java.io.BufferedInputStream is = new java.io.BufferedInputStream(inputStream)) {
				byte[] bytes = new byte[(int) blob.length()];
				int len = bytes.length;
				int offset = 0;
				int read = 0;
				while (offset < len && (read = is.read(bytes, offset, len - offset)) >= 0) {
					offset += read;
				}
				return bytes;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String clob2Str(java.sql.Clob clob) {
		try (java.io.Reader is = clob.getCharacterStream()) {
			java.io.BufferedReader reader = new java.io.BufferedReader(is);
			StringBuilder sb = new StringBuilder();
			char[] buffer = new char[4096];
			for (int i = reader.read(buffer); i > 0; i = reader.read(buffer)) {
				sb.append(buffer, 0, i);
			}
			return sb.toString();
		} catch (SQLException | java.io.IOException e) {
			log.warn("Field Value convert from java.sql.Clob to java.lang.String failed:", e);
			return null;
		}
	}

	private static byte[] toByteArray(Object obj) {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
			 ObjectOutputStream oos = new ObjectOutputStream(bos)) {
			oos.writeObject(obj);
			oos.flush();
			return bos.toByteArray();
		} catch (Exception e) {
			log.error("Field value convert from {} to byte[] failed:", obj.getClass().getName(), e);
			throw new RuntimeException(e);
		}
	}

}
