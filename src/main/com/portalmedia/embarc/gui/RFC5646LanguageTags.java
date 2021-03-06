package com.portalmedia.embarc.gui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class RFC5646LanguageTags {

	HashMap<String, String> values = new HashMap<String, String>();

	public RFC5646LanguageTags() {
		values.put("", "");
		values.put("af", "Afrikaans");
		values.put("af-ZA", "Afrikaans (South Africa)");
		values.put("ar", "Arabic");
		values.put("ar-AE", "Arabic (U.A.E.)");
		values.put("ar-BH", "Arabic (Bahrain)");
		values.put("ar-DZ", "Arabic (Algeria)");
		values.put("ar-EG", "Arabic (Egypt)");
		values.put("ar-IQ", "Arabic (Iraq)");
		values.put("ar-JO", "Arabic (Jordan)");
		values.put("ar-KW", "Arabic (Kuwait)");
		values.put("ar-LB", "Arabic (Lebanon)");
		values.put("ar-LY", "Arabic (Libya)");
		values.put("ar-MA", "Arabic (Morocco)");
		values.put("ar-OM", "Arabic (Oman)");
		values.put("ar-QA", "Arabic (Qatar)");
		values.put("ar-SA", "Arabic (Saudi Arabia)");
		values.put("ar-SY", "Arabic (Syria)");
		values.put("ar-TN", "Arabic (Tunisia)");
		values.put("ar-YE", "Arabic (Yemen)");
		values.put("az", "Azeri (Latin)");
		values.put("az-AZ", "Azeri (Latin) (Azerbaijan)");
		values.put("az-Cyrl-AZ", "Azeri (Cyrillic) (Azerbaijan)");
		values.put("be", "Belarusian");
		values.put("be-BY", "Belarusian (Belarus)");
		values.put("bg", "Bulgarian");
		values.put("bg-BG", "Bulgarian (Bulgaria)");
		values.put("bs-BA", "Bosnian (Bosnia and Herzegovina)");
		values.put("ca", "Catalan");
		values.put("ca-ES", "Catalan (Spain)");
		values.put("cs", "Czech");
		values.put("cs-CZ", "Czech (Czech Republic)");
		values.put("cy", "Welsh");
		values.put("cy-GB", "Welsh (United Kingdom)");
		values.put("da", "Danish");
		values.put("da-DK", "Danish (Denmark)");
		values.put("de", "German");
		values.put("de-AT", "German (Austria)");
		values.put("de-CH", "German (Switzerland)");
		values.put("de-DE", "German (Germany)");
		values.put("de-LI", "German (Liechtenstein)");
		values.put("de-LU", "German (Luxembourg)");
		values.put("dv", "Divehi");
		values.put("dv-MV", "Divehi (Maldives)");
		values.put("el", "Greek");
		values.put("el-GR", "Greek (Greece)");
		values.put("en", "English");
		values.put("en-AU", "English (Australia)");
		values.put("en-BZ", "English (Belize)");
		values.put("en-CA", "English (Canada)");
		values.put("en-CB", "English (Caribbean)");
		values.put("en-GB", "English (United Kingdom)");
		values.put("en-IE", "English (Ireland)");
		values.put("en-JM", "English (Jamaica)");
		values.put("en-NZ", "English (New Zealand)");
		values.put("en-PH", "English (Republic of the Philippines)");
		values.put("en-TT", "English (Trinidad and Tobago)");
		values.put("en-US", "English (United States)");
		values.put("en-ZA", "English (South Africa)");
		values.put("en-ZW", "English (Zimbabwe)");
		values.put("eo", "Esperanto");
		values.put("es", "Spanish");
		values.put("es-AR", "Spanish (Argentina)");
		values.put("es-BO", "Spanish (Bolivia)");
		values.put("es-CL", "Spanish (Chile)");
		values.put("es-CO", "Spanish (Colombia)");
		values.put("es-CR", "Spanish (Costa Rica)");
		values.put("es-DO", "Spanish (Dominican Republic)");
		values.put("es-EC", "Spanish (Ecuador)");
		values.put("es-ES", "Spanish (Spain)");
		values.put("es-GT", "Spanish (Guatemala)");
		values.put("es-HN", "Spanish (Honduras)");
		values.put("es-MX", "Spanish (Mexico)");
		values.put("es-NI", "Spanish (Nicaragua)");
		values.put("es-PA", "Spanish (Panama)");
		values.put("es-PE", "Spanish (Peru)");
		values.put("es-PR", "Spanish (Puerto Rico)");
		values.put("es-PY", "Spanish (Paraguay)");
		values.put("es-SV", "Spanish (El Salvador)");
		values.put("es-UY", "Spanish (Uruguay)");
		values.put("es-VE", "Spanish (Venezuela)");
		values.put("et", "Estonian");
		values.put("et-EE", "Estonian (Estonia)");
		values.put("eu", "Basque");
		values.put("eu-ES", "Basque (Spain)");
		values.put("fa", "Farsi");
		values.put("fa-IR", "Farsi (Iran)");
		values.put("fi", "Finnish");
		values.put("fi-FI", "Finnish (Finland)");
		values.put("fo", "Faroese");
		values.put("fo-FO", "Faroese (Faroe Islands)");
		values.put("fr", "French");
		values.put("fr-BE", "French (Belgium)");
		values.put("fr-CA", "French (Canada)");
		values.put("fr-CH", "French (Switzerland)");
		values.put("fr-FR", "French (France)");
		values.put("fr-LU", "French (Luxembourg)");
		values.put("fr-MC", "French (Principality of Monaco)");
		values.put("gl", "Galician");
		values.put("gl-ES", "Galician (Spain)");
		values.put("gu", "Gujarati");
		values.put("gu-IN", "Gujarati (India)");
		values.put("he", "Hebrew");
		values.put("he-IL", "Hebrew (Israel)");
		values.put("hi", "Hindi");
		values.put("hi-IN", "Hindi (India)");
		values.put("hr", "Croatian");
		values.put("hr-BA", "Croatian (Bosnia and Herzegovina)");
		values.put("hr-HR", "Croatian (Croatia)");
		values.put("hu", "Hungarian");
		values.put("hu-HU", "Hungarian (Hungary)");
		values.put("hy", "Armenian");
		values.put("hy-AM", "Armenian (Armenia)");
		values.put("id", "Indonesian");
		values.put("id-ID", "Indonesian (Indonesia)");
		values.put("is", "Icelandic");
		values.put("is-IS", "Icelandic (Iceland)");
		values.put("it", "Italian");
		values.put("it-CH", "Italian (Switzerland)");
		values.put("it-IT", "Italian (Italy)");
		values.put("ja", "Japanese");
		values.put("ja-JP", "Japanese (Japan)");
		values.put("ka", "Georgian");
		values.put("ka-GE", "Georgian (Georgia)");
		values.put("kk", "Kazakh");
		values.put("kk-KZ", "Kazakh (Kazakhstan)");
		values.put("kn", "Kannada");
		values.put("kn-IN", "Kannada (India)");
		values.put("ko", "Korean");
		values.put("ko-KR", "Korean (Korea)");
		values.put("kok", "Konkani");
		values.put("kok-IN", "Konkani (India)");
		values.put("ky", "Kyrgyz");
		values.put("ky-KG", "Kyrgyz (Kyrgyzstan)");
		values.put("lt", "Lithuanian");
		values.put("lt-LT", "Lithuanian (Lithuania)");
		values.put("lv", "Latvian");
		values.put("lv-LV", "Latvian (Latvia)");
		values.put("mi", "Maori");
		values.put("mi-NZ", "Maori (New Zealand)");
		values.put("mk", "FYRO Macedonian");
		values.put("mk-MK", "FYRO Macedonian (Former Yugoslav Republic of Macedonia)");
		values.put("mn", "Mongolian");
		values.put("mn-MN", "Mongolian (Mongolia)");
		values.put("mr", "Marathi");
		values.put("mr-IN", "Marathi (India)");
		values.put("ms", "Malay");
		values.put("ms-BN", "Malay (Brunei Darussalam)");
		values.put("ms-MY", "Malay (Malaysia)");
		values.put("mt", "Maltese");
		values.put("mt-MT", "Maltese (Malta)");
		values.put("nb", "Norwegian (Bokm?l)");
		values.put("nb-NO", "Norwegian (Bokm?l) (Norway)");
		values.put("nl", "Dutch");
		values.put("nl-BE", "Dutch (Belgium)");
		values.put("nl-NL", "Dutch (Netherlands)");
		values.put("nn-NO", "Norwegian (Nynorsk) (Norway)");
		values.put("ns", "Northern Sotho");
		values.put("ns-ZA", "Northern Sotho (South Africa)");
		values.put("pa", "Punjabi");
		values.put("pa-IN", "Punjabi (India)");
		values.put("pl", "Polish");
		values.put("pl-PL", "Polish (Poland)");
		values.put("ps", "Pashto");
		values.put("ps-AR", "Pashto (Afghanistan)");
		values.put("pt", "Portuguese");
		values.put("pt-BR", "Portuguese (Brazil)");
		values.put("pt-PT", "Portuguese (Portugal)");
		values.put("qu", "Quechua");
		values.put("qu-BO", "Quechua (Bolivia)");
		values.put("qu-EC", "Quechua (Ecuador)");
		values.put("qu-PE", "Quechua (Peru)");
		values.put("ro", "Romanian");
		values.put("ro-RO", "Romanian (Romania)");
		values.put("ru", "Russian");
		values.put("ru-RU", "Russian (Russia)");
		values.put("sa", "Sanskrit");
		values.put("sa-IN", "Sanskrit (India)");
		values.put("se", "Sami");
		values.put("se-FI", "Sami (Finland)");
		values.put("se-NO", "Sami (Norway)");
		values.put("se-SE", "Sami (Sweden)");
		values.put("sk", "Slovak");
		values.put("sk-SK", "Slovak (Slovakia)");
		values.put("sl", "Slovenian");
		values.put("sl-SI", "Slovenian (Slovenia)");
		values.put("sq", "Albanian");
		values.put("sq-AL", "Albanian (Albania)");
		values.put("sr-BA", "Serbian (Latin) (Bosnia and Herzegovina)");
		values.put("sr-Cyrl-BA", "Serbian (Cyrillic) (Bosnia and Herzegovina)");
		values.put("sr-SP", "Serbian (Latin) (Serbia and Montenegro)");
		values.put("sr-Cyrl-SP", "Serbian (Cyrillic) (Serbia and Montenegro)");
		values.put("sv", "Swedish");
		values.put("sv-FI", "Swedish (Finland)");
		values.put("sv-SE", "Swedish (Sweden)");
		values.put("sw", "Swahili");
		values.put("sw-KE", "Swahili (Kenya)");
		values.put("syr", "Syriac");
		values.put("syr-SY", "Syriac (Syria)");
		values.put("ta", "Tamil");
		values.put("ta-IN", "Tamil (India)");
		values.put("te", "Telugu");
		values.put("te-IN", "Telugu (India)");
		values.put("th", "Thai");
		values.put("th-TH", "Thai (Thailand)");
		values.put("tl", "Tagalog");
		values.put("tl-PH", "Tagalog (Philippines)");
		values.put("tn", "Tswana");
		values.put("tn-ZA", "Tswana (South Africa)");
		values.put("tr", "Turkish");
		values.put("tr-TR", "Turkish (Turkey)");
		values.put("tt", "Tatar");
		values.put("tt-RU", "Tatar (Russia)");
		values.put("ts", "Tsonga");
		values.put("uk", "Ukrainian");
		values.put("uk-UA", "Ukrainian (Ukraine)");
		values.put("ur", "Urdu");
		values.put("ur-PK", "Urdu (Islamic Republic of Pakistan)");
		values.put("uz", "Uzbek (Latin)");
		values.put("uz-UZ", "Uzbek (Latin) (Uzbekistan)");
		values.put("uz-Cyrl-UZ", "Uzbek (Cyrillic) (Uzbekistan)");
		values.put("vi", "Vietnamese");
		values.put("vi-VN", "Vietnamese (Viet Nam)");
		values.put("xh", "Xhosa");
		values.put("xh-ZA", "Xhosa (South Africa)");
		values.put("zh", "Chinese");
		values.put("zh-CN", "Chinese (S)");
		values.put("zh-HK", "Chinese (Hong Kong)");
		values.put("zh-MO", "Chinese (Macau)");
		values.put("zh-SG", "Chinese (Singapore)");
		values.put("zh-TW", "Chinese (T)");
		values.put("zu", "Zulu");
		values.put("zu-ZA", "Zulu (South Africa)");
	}

	public String getValue(String key) {
		return values.get(key);
	}

	public Collection<String> getValues(){
		return values.values();
	}
	
	public HashMap<String, String> getMap() {
		return values;
	}
	
	public TreeMap<String, String> getTreeMap() {
		return new TreeMap<String, String>(values);
	}

	public String getKey(String description) {
		Iterator<Entry<String, String>> hmIterator = values.entrySet().iterator();
		while (hmIterator.hasNext()) {
			Map.Entry mapElement = (Map.Entry)hmIterator.next();
			if (mapElement.getValue().equals(description)) {
				return (String) mapElement.getKey();
			}
		}
		return "???";
	}
}
