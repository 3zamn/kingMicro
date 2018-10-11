package com.king.common.utils.exception;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import com.king.common.utils.thread.SensibleClone;

/**
 * 异常输出工具类
 * @author King chen
 * @emai 396885563@qq.com
 * @date 2018年7月13日
 */
public class ExceptionUtils {
	static class ThrowableCreatedElsewhere extends Throwable {
		private static final long serialVersionUID = 1L;

		public ThrowableCreatedElsewhere(Throwable throwable) {
			super(throwable.getClass() + " created elsewhere");
			this.setStackTrace(throwable.getStackTrace());
		}

		@Override
		public Throwable fillInStackTrace() {
			return this;
		}
	}

	public static <T extends Throwable & SensibleClone<T>> T fixStackTrace(
			T throwable) {
		throwable = throwable.sensibleClone();

		if (throwable.getCause() == null) {
			try {
				throwable.initCause(new ThrowableCreatedElsewhere(throwable));
			} catch (IllegalStateException e) {
			}
		}

		throwable.fillInStackTrace();
		StackTraceElement[] existing = throwable.getStackTrace();
		StackTraceElement[] newTrace = new StackTraceElement[existing.length - 1];
		System.arraycopy(existing, 1, newTrace, 0, newTrace.length);
		throwable.setStackTrace(newTrace);
		return throwable;
	}

	public static String makeStackTrace(Throwable throwable) {
		ByteArrayOutputStream baOutStream = new ByteArrayOutputStream();
		PrintStream printStream = new PrintStream(baOutStream, false);
		throwable.printStackTrace(printStream);
		printStream.flush(); 
		String text = baOutStream.toString();
		printStream.close();
		return text;
	}
}
