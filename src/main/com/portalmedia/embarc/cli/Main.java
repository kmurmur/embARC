package com.portalmedia.embarc.cli;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.portalmedia.embarc.gui.helper.DPXFileListHelper;
import com.portalmedia.embarc.gui.helper.MXFFileList;
import com.portalmedia.embarc.gui.mxf.MXFProfileULMap;
import com.portalmedia.embarc.parser.ColumnDef;
import com.portalmedia.embarc.parser.FileFormat;
import com.portalmedia.embarc.parser.FileFormatDetection;
import com.portalmedia.embarc.parser.FileInformation;
import com.portalmedia.embarc.parser.MetadataColumn;
import com.portalmedia.embarc.parser.MetadataColumnDef;
import com.portalmedia.embarc.parser.dpx.DPXFileInformation;
import com.portalmedia.embarc.parser.dpx.DPXMetadata;
import com.portalmedia.embarc.parser.mxf.DescriptorHelper;
import com.portalmedia.embarc.parser.mxf.DeviceSetHelper;
import com.portalmedia.embarc.parser.mxf.IdentifierSetHelper;
import com.portalmedia.embarc.parser.mxf.MXFColumn;
import com.portalmedia.embarc.parser.mxf.MXFFileDescriptorResult;
import com.portalmedia.embarc.parser.mxf.MXFMetadata;
import com.portalmedia.embarc.parser.mxf.MXFService;
import com.portalmedia.embarc.parser.mxf.MXFServiceImpl;
import com.portalmedia.embarc.validation.CustomValidationRuleService;

import tv.amwa.maj.model.impl.AS07CoreDMSDeviceObjectsImpl;
import tv.amwa.maj.model.impl.AS07DMSIdentifierSetImpl;

/**
* Main class. Starts CLI
*
* @author PortalMedia
* @version 1.0
* @since 2020-01-20
*/
public class Main {

	static PrintStream consolePrintStream;
	static PrintStream nullPrintStream;
	static boolean isDPX = false;
	static boolean isMXF = false;
	static List<String> validDPXFileList = new ArrayList<>();
	static List<String> validMXFFileList = new ArrayList<>();
	static List<String> invalidFileList = new ArrayList<>();
	static List<String> validFileFailures = new ArrayList<>();
	static TreeMap<String, DPXFileInformation> dpxTreeMap = new TreeMap<String, DPXFileInformation>();
	static TreeMap<String, FileInformation<MXFMetadata>> mxfTreeMap = new TreeMap<String, FileInformation<MXFMetadata>>();
	static Options options;
	static HelpFormatter formatter;

	/**
	* Runs the embarc CLI
	*
	* @param args an array of String arguments to be parsed
	* @throws IOException 
	*/
	public static void main(String[] args) throws IOException {

		consolePrintStream = System.out;
		nullPrintStream = new PrintStream(new NullOutputStream());

		CommandLineParser parser = new DefaultParser();

		options = new Options();
		Option printHelpOption = Option.builder("help").desc("Print this list of options").build();
		Option printMetadataOption = Option.builder("print").desc("Print all metadata").build();
		Option outputCSVOption = Option.builder("csv").desc("DPX: CSV formatted output").hasArg().build();
		Option outputJSONOption = Option.builder("json").desc("DPX: JSON formatted output").hasArg().build();
		Option conformanceInputJSON = Option.builder("conformanceInputJSON").desc("DPX: Input validation json file").hasArg().build();
		Option conformanceOutputCSV = Option.builder("conformanceOutputCSV").desc("DPX: Output validation report csv file").hasArg().build();
		Option downloadTDStream = Option.builder("downloadTDStream").desc("MXF: Select a text stream to download").hasArg().build();
		Option downloadBDStream = Option.builder("downloadBDStream").desc("MXF: Select a binary stream to download").hasArg().build();
		Option downloadStreamOutputPath = Option.builder("streamOutputPath").desc("MXF: Output directory for selected stream").hasArg().build();

		options.addOption(printHelpOption);
		options.addOption(printMetadataOption);
		options.addOption(outputCSVOption);
		options.addOption(outputJSONOption);
		options.addOption(conformanceInputJSON);
		options.addOption(conformanceOutputCSV);
		options.addOption(downloadTDStream);
		options.addOption(downloadBDStream);
		options.addOption(downloadStreamOutputPath);

		formatter = new HelpFormatter();
		formatter.setOptionComparator(null);
		//formatter.printHelp("embARC DPX CLI", options);

		try {
			final CommandLine line = parser.parse(options, args);

			List<String> otherArgs = line.getArgList();

			if (line.hasOption("help")) {
				formatter.printHelp("embARC CLI", options);
				return;
			}

			if (otherArgs.size() > 1) {
				System.out.println("\nToo many arguments\n");
				return;
			} else if (otherArgs.size() == 0) {
				System.out.println("\nInput path is missing\n");
				return;
			}

			String inputPath = otherArgs.get(0);
			System.out.println("\nInput Path: " + inputPath);

			// determine what type of files were submitted and populate file lists
			processInput(inputPath);

			// if DPX or MXF identified, process accordingly
			if (isDPX || isMXF) {
				if (isDPX) processDPXInput(line);
				if (isMXF) processMXFInput(line);

				System.out.println("\n-- END --\n");
				return;
			}

			// if no valid files are identified, print messsage and end program
			System.out.println("\nNo valid DPX or MXF files were found.");
			System.out.println("\n-- END --\n");
		} catch(ParseException exp) {
			System.out.println("Unexpected exception:" + exp.getMessage());
		}
	}

	private static void processDPXInput(CommandLine line) throws IOException {
		boolean hasOption = false;

		String jsonPath = "";
		String csvPath = "";

		if (line.hasOption("csv")) {
			hasOption = true;
			csvPath = line.getOptionValue("csv");
			System.out.println("\nCSV Output Path: " + csvPath);
		}

		if (line.hasOption("json")) {
			hasOption = true;
			jsonPath = line.getOptionValue("json");
			System.out.println("\nJSON Output Path: " + jsonPath);;
		}

		// if this is a batch of files, process batch
		if (dpxTreeMap.size() > 1) {
			System.out.println("\n-- SEQUENCE SUMMARY --");
			System.out.format("%-18s%-1d", "\nTotal files: ", validDPXFileList.size() + invalidFileList.size());
			System.out.format("%-18s%-1d", "\nDPX files: ", validDPXFileList.size());
			System.out.format("%-18s%-1d", "\nNon-DPX files: ", invalidFileList.size());
			System.out.println("\n");
			BatchProcessorDpx.processBatch(dpxTreeMap);
		}

		if (!csvPath.isEmpty()) CsvWriterDpx.writeCsvDPXFiles(csvPath, dpxTreeMap);

		if (!jsonPath.isEmpty()) JsonWriterDpx.writeJsonDPXFiles(jsonPath, dpxTreeMap);

		if (line.hasOption("print")) {
			hasOption = true;
			System.out.println("\n-- DPX FILE METADATA --");
			for (DPXFileInformation dpxFile : dpxTreeMap.values()) printDPXMetadata(dpxFile);
		}

		if (line.hasOption("conformanceInputJSON")) {
			hasOption = true;
			String inputJSONPath = line.getOptionValue("conformanceInputJSON");
			String outputCSVPath = "";
			if (line.hasOption("conformanceOutputCSV")) {
				outputCSVPath = line.getOptionValue("conformanceOutputCSV");
			}
			CustomValidationRuleService.readRuleSet(inputJSONPath, outputCSVPath, dpxTreeMap);
		}

		if (!hasOption) noOptionsSpecified();
	}

	private static void processMXFInput(CommandLine line) {
		boolean hasOption = false;

		if (mxfTreeMap.size() > 1) {
			System.out.println("\nMXF parsing is limited to a single file. Please adjust input and try again.");
			return;
		}

		if (line.hasOption("print")) {
			hasOption = true;
			System.out.println("\n-- MXF FILE METADATA --");
			for (FileInformation<MXFMetadata> mxfFile : mxfTreeMap.values()) printMXFMetadata(mxfFile);
		}

		if (line.hasOption("downloadTDStream")) {
			hasOption = true;
			System.out.println("\n-- DOWNLOAD TEXT STREAM --");
			String streamId = line.getOptionValue("downloadTDStream");
			String streamOutputPath = "";
			if (line.hasOption("streamOutputPath")) {
				streamOutputPath = line.getOptionValue("streamOutputPath");
			}
			downloadGenericStream(streamId, streamOutputPath);
		}

		if (line.hasOption("downloadBDStream")) {
			hasOption = true;
			System.out.println("\n-- DOWNLOAD BINARY STREAM --");
			String streamId = line.getOptionValue("downloadBDStream");
			String streamOutputPath = "";
			if (line.hasOption("streamOutputPath")) {
				streamOutputPath = line.getOptionValue("streamOutputPath");
			}
			downloadGenericStream(streamId, streamOutputPath);
		}

		if (!hasOption) noOptionsSpecified();
	}

	private static void noOptionsSpecified() {
		System.out.println("\nNo options specified. Please include at least one option.\n");
		formatter.printHelp("embARC CLI", options);
		return;
	}

	/*******************************/
	/** Print DPX Metadata        **/
	/*******************************/

	private static void printDPXMetadata(DPXFileInformation dpxFileInfo) {
		System.out.println("\n" + dpxFileInfo.getName());

		DPXMetadata data = dpxFileInfo.getFileData();
		LinkedHashMap<ColumnDef, MetadataColumn> metadata = data.getMetadataHashMap();

		int offset = 0;
		String currentSection = "";
		String currentSubsection = "";

		for (Map.Entry<ColumnDef, MetadataColumn> entry : metadata.entrySet()) {
			ColumnDef key = entry.getKey();
			MetadataColumn value = entry.getValue();

			if (currentSection != key.getSectionDisplayName()) {
				currentSection = key.getSectionDisplayName();
				System.out.println("\n" + currentSection);
				System.out.println("--------------------------------------------------------------------------------");
			}

			if (currentSection == "Image Information") {
				String subsection = key.getSubsection().getDisplayName();
				if (subsection != currentSubsection && subsection != "") {
					currentSubsection = subsection;
					System.out.println("  --- " + currentSubsection + " ---");
				}
			}

			System.out.format("%-2s%-8d%-40s%-1s\n", "", offset, key.getDisplayName(), value.getStandardizedValue());
			offset += key.getLength();
		}
	}

	/*******************************/
	/** Print MXF Metadata        **/
	/*******************************/

	private static void printMXFMetadata(FileInformation<MXFMetadata> mxfFileInfo) {
		System.out.println("\n" + mxfFileInfo.getName());

		MXFMetadata data = mxfFileInfo.getFileData();

		System.out.println("\nFile Information");
		System.out.println("--------------------------------------------------------------------------------");

		System.out.format("%-35s%-1s\n", "File Path", "" + mxfFileInfo.getPath());
		System.out.format("%-35s%-1s\n", "Format", data.getFormat());
		System.out.format("%-35s%-1s\n", "Version", data.getVersion());
		System.out.format("%-35s%-1s\n", "Profile", mapProfileToControlledList(data.getProfile()));
		System.out.format("%-35s%-1s\n", "File Size", data.getFileSize());
		System.out.format("%-35s%-1s\n", "Video Track Count", data.getVideoTrackCount());
		System.out.format("%-35s%-1s\n", "Audio Track Count", data.getAudioTrackCount());
		System.out.format("%-35s%-1s\n", "Caption Track Count", data.getCaptionTrackCount());
		System.out.format("%-35s%-1s\n", "Timecode Track Count", data.getTimecodeTrackCount());
		System.out.format("%-35s%-1s\n", "TD Count", data.getTDCount());
		System.out.format("%-35s%-1s\n", "BD Count", data.getBDCount());

		printAS07CoreDMS(data);

		MXFFileDescriptorResult descriptors = data.getFileDescriptors();
		printDescriptorData(descriptors);

		HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>> tdData = data.getTDColumns();
		printTextAndBinaryData(tdData, "Text");

		HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>> bdData = data.getBDColumns();
		printTextAndBinaryData(bdData, "Binary");
	}

	private static String mapProfileToControlledList(String profileUl) {
		String stripped = profileUl.replace("urn:smpte:ul:", "");
		HashMap<String, String> profileULMap = new MXFProfileULMap().getMap();
		String val = profileULMap.get(stripped);
		if (val == null || val == "") return profileUl;
		return val += " (" + profileUl + ")";
	}

	private static void printAS07CoreDMS(MXFMetadata data) {
		System.out.println("\nAS07 Core DMS");
		System.out.println("--------------------------------------------------------------------------------");

		HashMap<MXFColumn, MetadataColumnDef> coreData = data.getCoreColumns();

		printCoreProperty(coreData, MXFColumn.AS_07_Core_DMS_ShimName, "Shim Name");
		printCoreProperty(coreData, MXFColumn.AS_07_Core_DMS_ResponsibleOrganizationName, "Responsible Organization Name");
		printCoreProperty(coreData, MXFColumn.AS_07_Core_DMS_ResponsibleOrganizationCode, "Responsible Organization Code");
		printCoreProperty(coreData, MXFColumn.AS_07_Core_DMS_NatureOfOrganization, "Nature of Organization");
		printCoreProperty(coreData, MXFColumn.AS_07_Core_DMS_WorkingTitle, "Working Title");
		printCoreProperty(coreData, MXFColumn.AS_07_Core_DMS_SecondaryTitle, "Secondary Title");
		printCoreProperty(coreData, MXFColumn.AS_07_Core_DMS_PictureFormat, "Picture Format");
		printCoreProperty(coreData, MXFColumn.AS_07_Core_DMS_IntendedAFD, "Intended AFD");
		printCoreProperty(coreData, MXFColumn.AS_07_Core_DMS_Captions, "Captions");
		printCoreProperty(coreData, MXFColumn.AS_07_Core_DMS_AudioTrackPrimaryLanguage, "Audio Primary Language");
		printCoreProperty(coreData, MXFColumn.AS_07_Core_DMS_AudioTrackSecondaryLanguage, "Audio Secondary Language");
		printCoreProperty(coreData, MXFColumn.AS_07_Core_DMS_AudioTrackLayout, "Audio Track Layout");
		printCoreProperty(coreData, MXFColumn.AS_07_Core_DMS_AudioTrackLayoutComment, "Audio Track Layout Comment");

		IdentifierSetHelper idSetHelper = new IdentifierSetHelper();
		String identifierString = coreData.get(MXFColumn.AS_07_Core_DMS_Identifiers).getCurrentValue();
		ArrayList<AS07DMSIdentifierSetImpl> identifiers = idSetHelper.createIdentifierListFromString(identifierString);
		if (identifiers.size() == 0) {
			System.out.format("%-35s%-1s\n", "Identifiers", "No Identifiers");
		} else {
			for (int i = 0; i < identifiers.size(); i++) {
				AS07DMSIdentifierSetImpl id = identifiers.get(i);
				System.out.format("%-35s\n", "Identifier " + (i+1) + ":");
				System.out.format("%-5s%-20s%-1s\n", "", "Type", id.getIdentifierType());
				System.out.format("%-5s%-20s%-1s\n", "", "Role", id.getIdentifierRole());
				System.out.format("%-5s%-20s%-1s\n", "", "Value", id.getIdentifierValue());
				System.out.format("%-5s%-20s%-1s\n", "", "Comment", id.getIdentifierComment());
			}
		}

		DeviceSetHelper deviceSetHelper = new DeviceSetHelper();
		String deviceString = coreData.get(MXFColumn.AS_07_Core_DMS_Devices).getCurrentValue();
		ArrayList<AS07CoreDMSDeviceObjectsImpl> devices = deviceSetHelper.createDeviceListFromString(deviceString);
		if (devices.size() == 0) {
			System.out.format("%-35s%-1s\n", "Devices", "No Devices");
		} else {
			for (int i = 0; i < devices.size(); i++) {
				AS07CoreDMSDeviceObjectsImpl device = devices.get(i);
				System.out.format("%-35s\n", "Device " + (i+1) + ":");
				System.out.format("%-5s%-20s%-1s\n", "", "Type", device.getDeviceType());
				System.out.format("%-5s%-20s%-1s\n", "", "Manufacturer", device.getManufacturer());
				System.out.format("%-5s%-20s%-1s\n", "", "Model", device.getModel());
				System.out.format("%-5s%-20s%-1s\n", "", "Serial Number", device.getSerialNumber());
				System.out.format("%-5s%-20s%-1s\n", "", "Usage Description", device.getUsageDescription());
			}
		}
	}

	private static void printCoreProperty(HashMap<MXFColumn, MetadataColumnDef> coreData, MXFColumn col, String label) {
		String value = "";
		if (coreData.containsKey(col)) {
			value = coreData.get(col).getCurrentValue();
		}
		System.out.format("%-35s%-1s\n", label, value);
	}

	private static void printTextAndBinaryData(HashMap<String, LinkedHashMap<MXFColumn, MetadataColumnDef>> data, String label) {
		System.out.println("\n" + label + " Data");
		System.out.println("--------------------------------------------------------------------------------");

		int elementCount = 1;
		
		if (data.keySet().size() == 0) {
			System.out.println("\nNo " + label + " data");
			return;
		}

		for (final String element : data.keySet()) {
			final LinkedHashMap<MXFColumn, MetadataColumnDef> item = data.get(element);
			System.out.println("\n" + label + " Data Element #" + elementCount + "\n");

			for (MXFColumn key : item.keySet()) {
				if (key == MXFColumn.AS_07_Object_TextBasedMetadataPayloadSchemeIdentifier ||
					key == MXFColumn.AS_07_TD_DMS_PrimaryRFC5646LanguageCode ||
					key == MXFColumn.AS_07_BD_DMS_PrimaryRFC5646LanguageCode ||
					key == MXFColumn.AS_07_Object_Identifiers) continue;

				String val = item.get(key).toString();
				System.out.format("%-35s%-1s\n", key.getDisplayName(), val);
			}

			for (MXFColumn key : item.keySet()) {
				if (key == MXFColumn.AS_07_Object_Identifiers) {
					IdentifierSetHelper idSetHelper = new IdentifierSetHelper();
					ArrayList<AS07DMSIdentifierSetImpl> identifiers = idSetHelper.createIdentifierListFromString(item.get(key).toString());
					for (int i = 0; i < identifiers.size(); i++) {
						AS07DMSIdentifierSetImpl id = identifiers.get(i);
						System.out.format("%-35s\n", "Identifier " + (i+1) + ": ");
						System.out.format("%-5s%-20s%-1s\n", "", "Value", id.getIdentifierValue().replace("urn:uuid:", ""));
						System.out.format("%-5s%-20s%-1s\n", "", "Role", id.getIdentifierRole());
						System.out.format("%-5s%-20s%-1s\n", "", "Type", id.getIdentifierType());
						System.out.format("%-5s%-20s%-1s\n", "", "Comment", id.getIdentifierComment());
					}
					continue;
				}
			}

			elementCount++;
		}
	}

	private static void printDescriptorData(MXFFileDescriptorResult descriptors) {
		System.out.println("\nDescriptors");
		System.out.println("--------------------------------------------------------------------------------");

		DescriptorHelper descriptorHelper = new DescriptorHelper();

		// picture descriptors: cdci
		ArrayList<LinkedHashMap<String, String>> cdciDescriptors = descriptorHelper.getPictureDescriptors(descriptors);
		System.out.println("\nPicture Descriptors (" + cdciDescriptors.size() + ")");
		for (LinkedHashMap<String, String> cdci : cdciDescriptors) {
			cdci.forEach((key, val) -> System.out.format("%-35s%-1s\n", key, val));
		}

		// sound descriptors: wave
		ArrayList<LinkedHashMap<String, String>> waveDescriptors = descriptorHelper.getSoundDescriptors(descriptors);
		System.out.println("\nSound Descriptors (" + waveDescriptors.size() + ")");
		for (LinkedHashMap<String, String> wave : waveDescriptors) {
			wave.forEach((key, val) -> System.out.format("%-35s%-1s\n", key, val));
		}

		// other descriptors: ancillary, timed text, datetime
		ArrayList<LinkedHashMap<String, String>> otherDescriptors = descriptorHelper.getOtherDescriptors(descriptors);
		System.out.println("\nOther Descriptors (" + otherDescriptors.size() + ")");
		for (LinkedHashMap<String, String> other : otherDescriptors) {
			other.forEach((key, val) -> System.out.format("%-35s%-1s\n", key, val));
		}
	}

	/*******************************/
	/** Input Processing **/
	/*******************************/

	private static void processInput(String inputPath) {
		getDirectoryContents(inputPath);

		if (validDPXFileList.size() > 0) {
			isDPX = true;

			for (String filePath : validDPXFileList) {
				DPXFileInformation dpxFile = DPXFileListHelper.createDPXFileInformation(filePath);
				dpxTreeMap.put(dpxFile.getName(), dpxFile);
			}

			return;
		}

		if (validMXFFileList.size() > 0) {
			isMXF = true;

			System.setOut(nullPrintStream);
			System.setErr(nullPrintStream);

			for (String filePath : validMXFFileList) {
				FileInformation<MXFMetadata> mxfFile = MXFFileList.getInstance().getFileInfo(filePath);
				mxfTreeMap.put(mxfFile.getName(), mxfFile);
			}

			System.setOut(consolePrintStream);
			System.setErr(consolePrintStream);

			return;
		}
	}

	private static void getDirectoryContents(String dir) {
		final Path folder = Paths.get(dir);
		if (!Files.isDirectory(folder)) {
			checkFileType(folder.toAbsolutePath().toString());
		} else {
			try (DirectoryStream<Path> stream = Files.newDirectoryStream(folder)) {
				for (final Path filePath : stream) {
					if (Files.isHidden(filePath)) continue;
					if (Files.isDirectory(filePath)) {
						getDirectoryContents(filePath.toAbsolutePath().toString());
					} else {
						checkFileType(filePath.toAbsolutePath().toString());
					}
				}
			} catch (final IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	private static void checkFileType(String f) {
		FileFormat fileFormat = FileFormatDetection.getFileFormat(f);
		if (fileFormat == FileFormat.DPX) {
			validDPXFileList.add(f);
		} else if (fileFormat == FileFormat.MXF) {
			validMXFFileList.add(f);
		} else {
			invalidFileList.add(f);
		}
		return;
	}

	private static void downloadGenericStream(String streamId, String userProvidedOutputPath) {
		// Just getting the first one here because there should only be one file
		FileInformation<MXFMetadata> mxfFile = mxfTreeMap.firstEntry().getValue();

		int streamInt;
		try {
			streamInt = Integer.parseInt(streamId);
		} catch (NumberFormatException ex) {
			System.out.println("Invalid stream ID input");
			return;
		}

		try {
			System.setOut(nullPrintStream);
			System.setErr(nullPrintStream);
			MXFService mxfService = new MXFServiceImpl(mxfFile.getPath());

			ByteBuffer bb = mxfService.GetGenericStream(Integer.parseInt(streamId));
			if (bb == null) {
				System.out.println("\nStream " + streamId + " not found");
				return;
			}
			String ext = FileFormatDetection.getExtension(bb);
			String fileType = "DATA_DOWNLOAD" + ext;
			System.setOut(consolePrintStream);
			System.setErr(consolePrintStream);

			String outputPath = "";
			if (!userProvidedOutputPath.isEmpty()) {
				outputPath = userProvidedOutputPath;
			} else {
				System.out.println("\nNo path provided, writing to current directory.");
				outputPath = ".";
			}

			File outputDir = new File(outputPath);
			if (!outputDir.exists()) {
				System.out.println("\nOutput directory does not exist. Please adjust input and try again.");
				return;
			}

			if (!outputDir.isDirectory()) {
				System.out.println("\nOutput path is not a directory. Please adjust input and try again.");
				return;
			}

			String fileSeparator = System.getProperty("file.separator");

			if (!outputPath.endsWith(fileSeparator)) {
				outputPath += fileSeparator;
			}

			String fullOutput = String.format("%s%s_%s_%s", outputPath, mxfFile.getName(), streamId, fileType);

			System.out.println("\nOutput path: " + fullOutput);

			System.setOut(nullPrintStream);
			System.setErr(nullPrintStream);

			mxfService.DownloadGenericStream(streamInt, fullOutput);

			System.setOut(consolePrintStream);
			System.setErr(consolePrintStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
