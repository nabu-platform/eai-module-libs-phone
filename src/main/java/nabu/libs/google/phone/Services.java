/*
* Copyright (C) 2018 Alexander Verbruggen
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

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

import nabu.libs.google.phone.types.Region;

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
	
	public static void main(String...args) {
		for (String region : PhoneNumberUtil.getInstance().getSupportedRegions()) {
			System.out.println("region '" + region + "': " + PhoneNumberUtil.getInstance().getCountryCodeForRegion(region));
		}
	}
	
	// the region is not necessary if it is a global number
	// otherwise the region is the region you are dialling from, not necessarily the region the phone number belongs in
	@WebResult(name = "details")
	public PhoneNumber parse(@WebParam(name = "number") @NotNull String number, @WebParam(name = "region") String region) throws NumberParseException {
		return number == null ? null : PhoneNumberUtil.getInstance().parseAndKeepRawInput(number, region == null ? null : region.toUpperCase());
	}
	
	@WebResult(name = "formatted")
	public String format(@WebParam(name = "number") String number, @WebParam(name = "region") String region, @WebParam(name = "format") PhoneNumberFormat format) throws NumberParseException {
		return number == null ? null : PhoneNumberUtil.getInstance().format(parse(number, region), format);
	}

	@WebResult(name = "countryCode")
	public String getCountryPrefix(@NotNull @WebParam(name = "countryCode") String countryCode) {
		// it is stored as uppercase
		return countryCode == null ? null : "+" + PhoneNumberUtil.getInstance().getCountryCodeForRegion(countryCode.toUpperCase());
	}
	
	@WebResult(name = "regions")
	public List<Region> supportedRegions() {
		ArrayList<Region> regions = new ArrayList<Region>();
		for (String supported : PhoneNumberUtil.getInstance().getSupportedRegions()) {
			Region region = new Region();
			// they seem to all be country codes stored in uppercase
			region.setCode(supported);
			region.setPrefix("+" + PhoneNumberUtil.getInstance().getCountryCodeForRegion(supported));
			regions.add(region);
		}
		return regions;
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
