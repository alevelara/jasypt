/*
 * =============================================================================
 * 
 *   Copyright (c) 2007-2010, The JASYPT team (http://www.jasypt.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package org.jasypt.spring.properties;

import java.util.Properties;

import org.jasypt.commons.CommonUtils;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.properties.PropertyValueEncryptionUtils;
import org.jasypt.util.text.TextEncryptor;
import org.springframework.web.context.support.ServletContextPropertyPlaceholderConfigurer;

/**
 * <p>
 * Subclass of
 * <tt>org.springframework.web.context.support.ServletContextPropertyPlaceholderConfigurer</tt>
 * which can make use of a {@link org.jasypt.encryption.StringEncryptor} or
 * {@link org.jasypt.util.text.TextEncryptor} object to decrypt servlet context parameter values
 * if they are encrypted in the loaded resource locations.
 * </p>
 * <p>
 * A value is considered "encrypted" when it appears surrounded by
 * <tt>ENC(...)</tt>, like:
 * </p>
 * <p>
 * <center> <tt>&lt;param-value&gt;ENC(!"DGAS24FaIO$)&lt;/param-value&gt;</tt> </center>
 * </p>
 * 
 * @since 1.7
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @deprecated Package renamed as org.jasypt.spring3.properties. Classes in 
 *             this package will be removed in 1.11.
 * 
 */
public final class EncryptableServletContextPropertyPlaceholderConfigurer 
        extends ServletContextPropertyPlaceholderConfigurer {
	/*
	 * Only one of these instances will be initialized, the other one will be
	 * null.
	 */
	private final StringEncryptor stringEncryptor;
	private final TextEncryptor textEncryptor;

	/**
	 * <p>
	 * Creates an <tt>EncryptableServletContextPropertyPlaceholderConfigurer</tt> instance
	 * which will use the passed {@link StringEncryptor} object to decrypt
	 * encrypted values.
	 * </p>
	 * 
	 * @param stringEncryptor
	 *            the {@link StringEncryptor} to be used do decrypt values. It
	 *            can not be null.
	 */
	public EncryptableServletContextPropertyPlaceholderConfigurer(
	        final StringEncryptor stringEncryptor) {
		super();
		CommonUtils.validateNotNull(stringEncryptor, "Encryptor cannot be null");
		this.stringEncryptor = stringEncryptor;
		this.textEncryptor = null;
	}

	/**
	 * <p>
	 * Creates an <tt>EncryptableServletContextPropertyPlaceholderConfigurer</tt> instance which will use the
	 * passed {@link TextEncryptor} object to decrypt encrypted values.
	 * </p>
	 * 
	 * @param textEncryptor
	 *            the {@link TextEncryptor} to be used do decrypt values. It can
	 *            not be null.
	 */
	public EncryptableServletContextPropertyPlaceholderConfigurer(final TextEncryptor textEncryptor) {
		super();
		CommonUtils.validateNotNull(textEncryptor, "Encryptor cannot be null");
		this.stringEncryptor = null;
		this.textEncryptor = textEncryptor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.beans.factory.config.PropertyResourceConfigurer#convertPropertyValue(java.lang.String)
	 */
	protected String convertPropertyValue(final String originalValue) {
		if (!PropertyValueEncryptionUtils.isEncryptedValue(originalValue)) {
			return originalValue;
		}
		if (this.stringEncryptor != null) {
			return PropertyValueEncryptionUtils.decrypt(originalValue,
					this.stringEncryptor);

		}
		return PropertyValueEncryptionUtils.decrypt(originalValue, this.textEncryptor);
	}
	
	
	
    /*
     * Spring's ServletContextPropertyPlaceholderConfigurer never creates a complete
     * map of properties, and so never really applies the "convertPropertyValue" method
     * to them. Instead it gets properties on the fly and returns them without conversion
     * (as of Spring 3.0.5).
     * 
     * This fix makes sure that variables are decrypted before being returned.
     */
    protected String resolvePlaceholder(final String placeholder, final Properties props) {
        return convertPropertyValue(super.resolvePlaceholder(placeholder, props));
    }
    
}
