package nabu.libs.google.phone;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.validation.constraints.NotNull;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberType;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

/**
 * For reasons unknown, the PhoneNumber class has empty strings _by default_ instead of null
 * Additionally, the setters don't accept null (they explicitly throw an NPE°
 * However, consider the XML formatting generates empty tags (tags with an empty string)
 * The XML parser sees an empty tag and interprets this by default as null
 * So basically after a marshal/unmarshal via XML, the setters are called with null, triggering an exception, however this should only be of consequence to the developer tool so currently we won't fix this.
 * 
 * The region you pass in is where you are dialling from, for example the same number can be expressed in many ways depending on where you are calling from
 * The region is optional if you have included the region code in the number
 * The regions service itself returns the original region the phone is from
 */
@WebService
public class Services {
	
	// the region is not necessary if it is a global number
	// otherwise the region is the region you are dialling from, not necessarily the region the phone number belongs in
	@WebResult(name = "details")
	public PhoneNumber parse(@WebParam(name = "number") @NotNull String number, @WebParam(name = "region") String region) throws NumberParseException {
		return number == null ? null : PhoneNumberUtil.getInstance().parseAndKeepRawInput(number, region == null ? null : region.toUpperCase());
	}
	
	@WebResult(name = "formatted")
	public String format(@WebParam(name = "number") String number, @WebParam(name = "region") String region, @WebParam(name = "format") PhoneNumberFormat format) throws NumberParseException {
		return PhoneNumberUtil.getInstance().format(parse(number, region), format);
	}
	
	@WebResult(name = "regions")
	public List<String> regions(@WebParam(name = "number") @NotNull String number, @WebParam(name = "region") String region) throws NumberParseException {
		return new ArrayList<String>(PhoneNumberUtil.getInstance().getRegionCodesForCountryCode(parse(number, region).getCountryCode()));
	}
	
	// requires geocoder library, not interesting enough for now as it is rather big (1.7mb) and presumably updated from time to time to remain accurate
//	@WebResult(name = "regions")
//	public List<String> timezones(@WebParam(name = "number") @NotNull String number, @WebParam(name = "region") String region) throws NumberParseException {
//		return PhoneNumberToTimeZonesMapper.getInstance().getTimeZonesForNumber(parse(number, region));
//	}
	
	@WebResult(name = "type")
	public PhoneNumberType type(@WebParam(name = "number") @NotNull String number, @WebParam(name = "region") String region) throws NumberParseException {
		return PhoneNumberUtil.getInstance().getNumberType(parse(number, region));
	}
	
	@WebResult(name = "valid")
	public boolean valid(@WebParam(name = "number") @NotNull String number, @WebParam(name = "region") String region) throws NumberParseException {
		return PhoneNumberUtil.getInstance().isValidNumber(parse(number, region));
	}
}