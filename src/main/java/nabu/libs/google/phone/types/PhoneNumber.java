package nabu.libs.google.phone.types;

public class PhoneNumber extends com.google.i18n.phonenumbers.Phonenumber.PhoneNumber {

	private static final long serialVersionUID = 1L;

	@Override
	public com.google.i18n.phonenumbers.Phonenumber.PhoneNumber setExtension(String value) {
		return super.setExtension(value == null ? "" : value);
	}

	@Override
	public com.google.i18n.phonenumbers.Phonenumber.PhoneNumber setPreferredDomesticCarrierCode(String value) {
		return super.setPreferredDomesticCarrierCode(value == null ? "" : value);
	}

	@Override
	public com.google.i18n.phonenumbers.Phonenumber.PhoneNumber setRawInput(String value) {
		return super.setRawInput(value == null ? "" : value);
	}

	
}
