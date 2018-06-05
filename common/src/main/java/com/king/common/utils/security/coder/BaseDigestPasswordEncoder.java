package com.king.common.utils.security.coder;

import com.king.common.utils.security.BasePasswordEncoder;

public abstract class BaseDigestPasswordEncoder extends BasePasswordEncoder {
	private boolean encodeHashAsBase64 = false;

	public boolean getEncodeHashAsBase64() {
		return this.encodeHashAsBase64;
	}

	public void setEncodeHashAsBase64(boolean encodeHashAsBase64) {
		this.encodeHashAsBase64 = encodeHashAsBase64;
	}
}

