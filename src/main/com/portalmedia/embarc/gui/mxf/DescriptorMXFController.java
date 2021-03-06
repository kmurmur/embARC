package com.portalmedia.embarc.gui.mxf;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.portalmedia.embarc.gui.model.AS07TimecodeLabelSubdescriptor;
import com.portalmedia.embarc.gui.model.MXFSelectedFilesSummary;
import com.portalmedia.embarc.parser.mxf.MXFFileDescriptorResult;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import tv.amwa.maj.enumeration.AlphaTransparencyType;
import tv.amwa.maj.enumeration.ColorSitingType;
import tv.amwa.maj.enumeration.ElectroSpatialFormulation;
import tv.amwa.maj.enumeration.FieldNumber;
import tv.amwa.maj.enumeration.SignalStandardType;
import tv.amwa.maj.exception.PropertyNotPresentException;
import tv.amwa.maj.model.CodecDefinition;
import tv.amwa.maj.model.ContainerDefinition;
import tv.amwa.maj.model.Locator;
import tv.amwa.maj.model.SubDescriptor;
import tv.amwa.maj.model.impl.AS07DateTimeDescriptorImpl;
import tv.amwa.maj.model.impl.AncillaryPacketsDescriptorImpl;
import tv.amwa.maj.model.impl.CDCIDescriptorImpl;
import tv.amwa.maj.model.impl.PictureDescriptorImpl;
import tv.amwa.maj.model.impl.RGBADescriptorImpl;
import tv.amwa.maj.model.impl.STLDescriptorImpl;
import tv.amwa.maj.model.impl.SoundDescriptorImpl;
import tv.amwa.maj.model.impl.TimedTextDescriptorImpl;
import tv.amwa.maj.model.impl.VBIDescriptorImpl;
import tv.amwa.maj.model.impl.WAVEPCMDescriptorImpl;
import tv.amwa.maj.record.AUID;
import tv.amwa.maj.record.Rational;

/**
 * UI component that displays MXF file descriptor data
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2020-07-02
 */
public class DescriptorMXFController extends AnchorPane {

	@FXML
	private Label sectionLabel;
    @FXML
    private VBox descriptorsVBox;

    private Accordion descriptorsAccordion;

	public DescriptorMXFController() {
		ControllerMediatorMXF.getInstance().registerGeneralMXF(this);
		final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DescriptorMXF.fxml"));
		fxmlLoader.setController(this);
		fxmlLoader.setRoot(this);
		try {
			fxmlLoader.load();
		} catch (final IOException exception) {
			throw new RuntimeException(exception);
		}
		descriptorsAccordion = new Accordion();
		descriptorsVBox.getChildren().add(descriptorsAccordion);
		descriptorsVBox.getStyleClass().add("descriptors-vbox");
		descriptorsAccordion.setMaxHeight(1000);
		descriptorsAccordion.setPrefHeight(700);
		final MXFSelectedFilesSummary summary = ControllerMediatorMXF.getInstance().getSelectedFilesSummary();
		setFileDescriptors(summary);
	}

	public void setTitle(String title) {
		sectionLabel.setText(title);
	}

	private void setFileDescriptors(MXFSelectedFilesSummary summary) {
		MXFFileDescriptorResult descriptors = summary.getFileDescriptors();
		if (Objects.isNull(descriptors)) {
			Label label = new Label();
			int fileCount = summary.getFileCount();
			if (fileCount == 0) {
				label.setText("No files selected, select a file to view descriptors.");
			} else if (fileCount > 1) {
				label.setText("Multiple files selected, please select only one file.");
			}
			descriptorsVBox.getChildren().clear();
			descriptorsVBox.getChildren().add(label);
		} else {
			setPictureDescriptors(descriptors);
			setSoundDescriptors(descriptors);
			setOtherDescriptors(descriptors);
		}
	}

	private void setPictureDescriptors(MXFFileDescriptorResult descriptors) {
		List<CDCIDescriptorImpl> cdciDescriptors = descriptors.getCDCIDescriptor();
		List<RGBADescriptorImpl> rgbaDescriptors = descriptors.getRGBADescriptors();
		List<PictureDescriptorImpl> pictureDescriptors = descriptors.getPictureDescriptors();

		ListView<BorderPane> pictureList = new ListView<BorderPane>();

		if (cdciDescriptors.size() == 0) {
			BorderPane noPicturePane = new BorderPane();
			Label noPicture = new Label("No Picture Descriptors Present");
			noPicturePane.getChildren().add(noPicture);
			pictureList.getItems().add(noPicturePane);
		} else {
			int index = 1;
			for (CDCIDescriptorImpl cdci : cdciDescriptors) {
				BorderPane card = createCDCICard(cdci, index);
				pictureList.getItems().add(card);
				index++;
			}
		}

		String title = "Picture Descriptors (" + pictureList.getItems().size() + ")";
		TitledPane tPane = new TitledPane(title, pictureList);
		descriptorsAccordion.getPanes().add(tPane);
		pictureList.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) { event.consume(); }
		});
	}

	private void setSoundDescriptors(MXFFileDescriptorResult descriptors) {
		List<WAVEPCMDescriptorImpl> wavePCMDescriptors = descriptors.getWavePCMDescriptors();
		List<SoundDescriptorImpl> soundDescriptors = descriptors.getSoundDescriptors();

		ListView<BorderPane> soundList = new ListView<BorderPane>();

		if (wavePCMDescriptors.size() == 0) {
			BorderPane noWavePane = new BorderPane();
			Label noWave = new Label("No Sound Descriptors Present");
			noWavePane.getChildren().add(noWave);
			soundList.getItems().add(noWavePane);
		} else {
			int index = 1;
			for (WAVEPCMDescriptorImpl wave : wavePCMDescriptors) {
				BorderPane card = createWAVECard(wave, index);
				soundList.getItems().add(card);
				index++;
			}
		}

		String title = "Sound Descriptors (" + soundList.getItems().size() + ")";
		TitledPane tPane = new TitledPane(title, soundList);
		descriptorsAccordion.getPanes().add(tPane);
		soundList.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) { event.consume(); }
		});
	}

	private void setOtherDescriptors(MXFFileDescriptorResult descriptors) {
		List<AncillaryPacketsDescriptorImpl> ancillaryDescriptors = descriptors.getAncillaryPacketsDescriptors();
		List<TimedTextDescriptorImpl> timedTextDescriptors = descriptors.getTimedTextDescriptor();
//		List<STLDescriptorImpl> stlDescriptors = descriptors.getSTLDescriptors();
//		List<VBIDescriptorImpl> vbiDescriptors = descriptors.getVBIDescriptors();
		List<AS07DateTimeDescriptorImpl> dateTimeDescriptors = descriptors.getAS07DateTimeDescriptor();

		ListView<BorderPane> otherList = new ListView<BorderPane>();
		int totalOtherDescriptors = (int)ancillaryDescriptors.size() + (int)dateTimeDescriptors.size() + (int)timedTextDescriptors.size();

		if (totalOtherDescriptors == 0) {
			BorderPane noOtherPane = new BorderPane();
			Label noOther = new Label("No Other Descriptors Present");
			noOtherPane.getChildren().add(noOther);
			otherList.getItems().add(noOtherPane);
		} else {
			int index = 1;
			for (AS07DateTimeDescriptorImpl dateTime : dateTimeDescriptors) {
				BorderPane card = createDateTimeCard(dateTime, index);
				otherList.getItems().add(card);
				index++;
			}
			for (AncillaryPacketsDescriptorImpl ancillary : ancillaryDescriptors) {
				BorderPane card = createAncillaryCard(ancillary, index);
				otherList.getItems().add(card);
				index++;
			}
			for (TimedTextDescriptorImpl timedText : timedTextDescriptors) {
				BorderPane card = createTimedTextCard(timedText, index);
				otherList.getItems().add(card);
				index++;
			}
		}

		String title = "Other Descriptors (" + otherList.getItems().size() + ")";
		TitledPane tPane = new TitledPane(title, otherList);
		descriptorsAccordion.getPanes().add(tPane);
		otherList.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) { event.consume(); }
		});
	}

	private BorderPane createCDCICard(CDCIDescriptorImpl cdci, int index) {
		BorderPane bp = new BorderPane();
		bp.setPadding(new Insets(10,0,10,0));
		GridPane topGrid = new GridPane();
		topGrid.setPadding(new Insets(10,10,10,10));
		Label label = new Label("CDCI Descriptor");
		label.setStyle("-fx-font-weight: bold;");
		topGrid.add(label, 0, 0);
		bp.setTop(topGrid);
		
		GridPane centerGrid = new GridPane();
		centerGrid.setPadding(new Insets(0,10,10,10));
		ColumnConstraints cc = new ColumnConstraints();
		cc.setPrefWidth(200);
		centerGrid.getColumnConstraints().add(cc);
		int row = 0;
		
		// File Descriptors Section
		centerGrid.add(getDescriptorLabel("File Descriptors", "#e3e3e3", 200, ""), 0, row);
		try {
		centerGrid.add(getDescriptorLabel("Essence Length: ", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + cdci.getEssenceLength(), "#e3e3e3", 400, ""), 2, row);
		row += 1;
		}
		catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}

		centerGrid.add(getDescriptorLabel("Sample Rate: ", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + cdci.getSampleRateString(), "#e3e3e3", 400, ""), 2, row);
		row += 1;

		centerGrid.add(getDescriptorLabel("Linked Track ID: ", "#e3e3e3", 200, "descriptor-border-bottom"), 1, row);
		centerGrid.add(getDescriptorLabel("" + cdci.getLinkedTrackID(), "#e3e3e3", 400, "descriptor-border-bottom"), 2, row);
		row += 1;

		// Picture Essence Descriptor Section
		centerGrid.add(getDescriptorLabel("Picture Essence Descriptors", "e3e3e3", 200, ""), 0, row);
		
		
		centerGrid.add(getDescriptorLabel("Signal Standard: ", "#e3e3e3", 200, ""), 1, row);
		try {
			SignalStandardType signalStandard = cdci.getSignalStandard();
			centerGrid.add(getDescriptorLabel("" + signalStandard, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;

		centerGrid.add(getDescriptorLabel("Picture Encoding: ", "#e3e3e3", 200, ""), 1, row);
		try {
			String picEncodingStr = cdci.getPictureCompression().toString();
			String stripped = picEncodingStr.replace("urn:smpte:ul:", "").toUpperCase();
			HashMap<String, String> picEncodingMap = new MXFPictureEncodingMap().getMap();
			String value = picEncodingMap.get(stripped);
			if (value == null || value == "") {
				value = picEncodingStr;
				centerGrid.add(getDescriptorLabel(value, "#e3e3e3", 400, ""), 2, row);
			} else {
				centerGrid.add(getDescriptorLabel(value, "#e3e3e3", 400, ""), 2, row);
				row += 1;
				centerGrid.add(getDescriptorLabel("", "#e3e3e3", 200, ""), 1, row);
				centerGrid.add(getDescriptorLabel("(" + stripped + ")", "#e3e3e3", 400, ""), 2, row);
			}
		} catch(PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		row += 1;

		centerGrid.add(getDescriptorLabel("Stored Height: ", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + cdci.getStoredHeight(), "#e3e3e3", 400, ""), 2, row);
		row += 1;

		centerGrid.add(getDescriptorLabel("Stored Width: ", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + cdci.getStoredWidth(), "#e3e3e3", 400, ""), 2, row);
		row += 1;

		centerGrid.add(getDescriptorLabel("Sampled Height: ", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + cdci.getSampledHeight(), "#e3e3e3", 400, ""), 2, row);
		row += 1;

		centerGrid.add(getDescriptorLabel("Sampled Width: ", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + cdci.getSampledWidth(), "#e3e3e3", 400, ""), 2, row);
		row += 1;

		centerGrid.add(getDescriptorLabel("Sampled X Offset: ", "#e3e3e3", 200, ""), 1, row);
		try {
			int sampledXOffset = cdci.getSampledXOffset();
			centerGrid.add(getDescriptorLabel("" + sampledXOffset, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;

		centerGrid.add(getDescriptorLabel("Sampled Y Offset: ", "#e3e3e3", 200, ""), 1, row);
		try {
			int sampledYOffset = cdci.getSampledYOffset();
			centerGrid.add(getDescriptorLabel("" + sampledYOffset, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;

		centerGrid.add(getDescriptorLabel("Display Height: ", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + cdci.getDisplayHeight(), "#e3e3e3", 400, ""), 2, row);
		row += 1;

		centerGrid.add(getDescriptorLabel("Display Width: ", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + cdci.getDisplayWidth(), "#e3e3e3", 400, ""), 2, row);
		row += 1;

		centerGrid.add(getDescriptorLabel("Display X Offset: ", "#e3e3e3", 200, ""), 1, row);
		try {
			int displayXOffset = cdci.getDisplayXOffset();
			centerGrid.add(getDescriptorLabel("" + displayXOffset, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;

		centerGrid.add(getDescriptorLabel("Display Y Offset: ", "#e3e3e3", 200, ""), 1, row);
		try {
			int displayYOffset = cdci.getDisplayYOffset();
			centerGrid.add(getDescriptorLabel("" + displayYOffset, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;

		centerGrid.add(getDescriptorLabel("Frame Layout: ", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + cdci.getFrameLayout(), "#e3e3e3", 400, ""), 2, row);
		row += 1;

		String videoLineMap = "";
		int[] arr = cdci.getVideoLineMap();
		if (arr != null && arr.length > 0) {
			for (int i = 0; i < arr.length; i++) {
				videoLineMap += arr[i];
				if (i < arr.length -1) videoLineMap += ", ";
			}
		}

		centerGrid.add(getDescriptorLabel("Video Line Map: ", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + videoLineMap, "#e3e3e3", 400, ""), 2, row);
		row += 1;

		centerGrid.add(getDescriptorLabel("Image Aspect Ratio: ", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + cdci.getImageAspectRatioString(), "#e3e3e3", 400, ""), 2, row);
		row += 1;

		centerGrid.add(getDescriptorLabel("Alpha Transparency: ", "#e3e3e3", 200, ""), 1, row);
		try {
			AlphaTransparencyType alphaTransparency = cdci.getAlphaTransparency();
			centerGrid.add(getDescriptorLabel("" + alphaTransparency, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;

		centerGrid.add(getDescriptorLabel("Image Alignment Offset: ", "#e3e3e3", 200, ""), 1, row);
		try {
			int alignmentFactor = cdci.getImageAlignmentFactor();
			centerGrid.add(getDescriptorLabel("" + alignmentFactor, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;

		centerGrid.add(getDescriptorLabel("Image Start Offset: ", "#e3e3e3", 200, ""), 1, row);
		try {
			int imageStartOffset = cdci.getImageStartOffset();
			centerGrid.add(getDescriptorLabel("" + imageStartOffset, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;

		centerGrid.add(getDescriptorLabel("Image End Offset: ", "#e3e3e3", 200, ""), 1, row);
		try {
			int imageEndOffset = cdci.getImageEndOffset();
			centerGrid.add(getDescriptorLabel("" + imageEndOffset, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;

		centerGrid.add(getDescriptorLabel("Field Dominance: ", "#e3e3e3", 200, ""), 1, row);
		try {
			FieldNumber fieldDominance = cdci.getFieldDominance();
			centerGrid.add(getDescriptorLabel("" + fieldDominance.value(), "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;

		centerGrid.add(getDescriptorLabel("Display F2 Offset: ", "#e3e3e3", 200, ""), 1, row);
		try {
			int displayOffset = cdci.getDisplayF2Offset();
			centerGrid.add(getDescriptorLabel("" + displayOffset, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;

		centerGrid.add(getDescriptorLabel("Stored F2 Offset: ", "#e3e3e3", 200, "descriptor-border-bottom"), 1, row);
		try {
			int storedOffset = cdci.getStoredF2Offset();
			centerGrid.add(getDescriptorLabel("" + storedOffset, "#e3e3e3", 400, "descriptor-border-bottom"), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, "descriptor-border-bottom"), 2, row);
		}
		row += 1;

		// CDCI Descriptor Section
		centerGrid.add(getDescriptorLabel("CDCI Descriptors", "#e3e3e3", 200, ""), 0, row);
		
		centerGrid.add(getDescriptorLabel("Active Format Descriptor: ", "#e3e3e3", 200, ""), 1, row);
		try {
			byte activeFormatDescriptor = cdci.getActiveFormatDescriptor();
			centerGrid.add(getDescriptorLabel("" + activeFormatDescriptor, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;

		centerGrid.add(getDescriptorLabel("Alpha Sample Depth: ", "#e3e3e3", 200, ""), 1, row);
		try {
			int alphaSampleDepth = cdci.getAlphaSampleDepth();
			centerGrid.add(getDescriptorLabel("" + alphaSampleDepth, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;

		centerGrid.add(getDescriptorLabel("Black Reference Level: ", "#e3e3e3", 200, ""), 1, row);
		try {
			int blackRefLevel = cdci.getBlackRefLevel();
			centerGrid.add(getDescriptorLabel("" + blackRefLevel, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;

		centerGrid.add(getDescriptorLabel("Color Range: ", "#e3e3e3", 200, ""), 1, row);
		try {
			int colorRange = cdci.getColorRange();
			centerGrid.add(getDescriptorLabel("" + colorRange, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;

		centerGrid.add(getDescriptorLabel("Color Siting: ", "#e3e3e3", 200, ""), 1, row);
		try {
			ColorSitingType colorSiting = cdci.getColorSiting();
			centerGrid.add(getDescriptorLabel("" + colorSiting, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;

		centerGrid.add(getDescriptorLabel("Component Depth: ", "#e3e3e3", 200, ""), 1, row);
		try {
			int componentDepth = cdci.getComponentDepth();
			centerGrid.add(getDescriptorLabel("" + componentDepth, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;

		centerGrid.add(getDescriptorLabel("Horizontal Subsampling: ", "#e3e3e3", 200, ""), 1, row);
		try {
			int horizontalSubsampling = cdci.getHorizontalSubsampling();
			centerGrid.add(getDescriptorLabel("" + horizontalSubsampling, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;

		centerGrid.add(getDescriptorLabel("Padding Bits: ", "#e3e3e3", 200, ""), 1, row);
		try {
			int paddingBits = cdci.getPaddingBits();
			centerGrid.add(getDescriptorLabel("" + paddingBits, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;

		centerGrid.add(getDescriptorLabel("Reversed Byte Order: ", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + cdci.getReversedByteOrder(), "#e3e3e3", 400, ""), 2, row);
		row += 1;

		centerGrid.add(getDescriptorLabel("Vertical Subsampling: ", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + cdci.getVerticalSubsampling(), "#e3e3e3", 400, ""), 2, row);
		row += 1;

		centerGrid.add(getDescriptorLabel("White Reference Level: ", "#e3e3e3", 200, ""), 1, row);
		try {
			int whiteRefLevel = cdci.getWhiteRefLevel();
			centerGrid.add(getDescriptorLabel("" + whiteRefLevel, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;
		centerGrid.add(getDescriptorLabel("", "", 200, ""),0,row);
		row += 1;

		// Calculated Values
		centerGrid.add(getDescriptorLabel("Calculated Values", "#e3e3e3", 200, ""), 0, row);

		centerGrid.add(getDescriptorLabel("Duration (seconds): ", "#e3e3e3", 200, ""), 1, row);
		try {
			long essenceLength = cdci.getEssenceLength();
			Rational sampleRate = cdci.getSampleRate();
			centerGrid.add(getDescriptorLabel("" + essenceLength/sampleRate.doubleValue(), "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("N/A", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;

		centerGrid.add(getDescriptorLabel("Samples Per Second: ", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + cdci.getSampleRate().doubleValue(), "#e3e3e3", 400, ""), 2, row);

		bp.setCenter(centerGrid);
		
		return bp;
	}

	private BorderPane createWAVECard(WAVEPCMDescriptorImpl wave, int index) {
		BorderPane bp = new BorderPane();
		bp.setPadding(new Insets(10,0,10,0));
		GridPane topGrid = new GridPane();
		topGrid.setPadding(new Insets(10,10,10,10));
		Label label = new Label("Wave Descriptor");
		label.setStyle("-fx-font-weight: bold;");
		topGrid.add(label, 0, 0);
		bp.setTop(topGrid);
		
		GridPane centerGrid = new GridPane();
		centerGrid.setPadding(new Insets(0,10,10,10));
		ColumnConstraints cc = new ColumnConstraints();
		cc.setPrefWidth(200);
		centerGrid.getColumnConstraints().add(cc);
		int row = 0;
		
		// File Descriptors Section
		centerGrid.add(getDescriptorLabel("File Descriptors", "#e3e3e3", 200, ""), 0, row);
		
		centerGrid.add(getDescriptorLabel("Instance UID: ", "#e3e3e3", 200, ""), 1, row);
		try {
			AUID instanceUID = wave.getOriginalAUID();
			centerGrid.add(getDescriptorLabel("" + instanceUID, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;
		
		centerGrid.add(getDescriptorLabel("Generation UID: ", "#e3e3e3", 200, ""), 1, row);
		try {
			AUID generation = wave.getLinkedGenerationID();
			centerGrid.add(getDescriptorLabel("" + generation, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;
		
		centerGrid.add(getDescriptorLabel("Linked Track ID: ", "#e3e3e3", 200, ""), 1, row);
		try {
			int linkedTrackID = wave.getLinkedTrackID();
			centerGrid.add(getDescriptorLabel("" + linkedTrackID, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;

		centerGrid.add(getDescriptorLabel("Sample Rate: ", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + wave.getSampleRateString(), "#e3e3e3", 400, ""), 2, row);
		row += 1;

		centerGrid.add(getDescriptorLabel("Container Duration: ", "#e3e3e3", 200, ""), 1, row);
		try {
			long essenceLength = wave.getEssenceLength();
			centerGrid.add(getDescriptorLabel("" + essenceLength, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;

		centerGrid.add(getDescriptorLabel("Codec: ", "#e3e3e3", 200, "descriptor-border-bottom"), 1, row);
		try {
			ContainerDefinition containerFormat = wave.getContainerFormat();
			centerGrid.add(getDescriptorLabel("" + containerFormat.getDescription(), "#e3e3e3", 400, "descriptor-border-bottom"), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, "descriptor-border-bottom"), 2, row);
		}
		row += 1;
		
		// Sound Descriptors Section
		centerGrid.add(getDescriptorLabel("Sound Descriptors", "#e3e3e3", 200, ""), 0, row);

		centerGrid.add(getDescriptorLabel("Audio Sample Rate: ", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + wave.getAudioSampleRateString(), "#e3e3e3", 400, ""), 2, row);
		row += 1;

		centerGrid.add(getDescriptorLabel("Channel Count: ", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + wave.getChannelCount(), "#e3e3e3", 400, ""), 2, row);
		row += 1;

		centerGrid.add(getDescriptorLabel("Sound Encoding: ", "#e3e3e3", 200, ""), 1, row);
		try {
			String soundEncodingStr = wave.getSoundCompression().toString();
			String stripped = soundEncodingStr.replace("urn:smpte:ul:", "").toUpperCase();
			HashMap<String, String> soundEncodingMap = new MXFSoundEncodingMap().getMap();
			String value = soundEncodingMap.get(stripped);
			if (value == null || value == "") {
				value = soundEncodingStr;
				centerGrid.add(getDescriptorLabel(value, "#e3e3e3", 400, ""), 2, row);
			} else {
				centerGrid.add(getDescriptorLabel(value, "#e3e3e3", 400, ""), 2, row);
				row += 1;
				centerGrid.add(getDescriptorLabel("", "#e3e3e3", 200, ""), 1, row);
				centerGrid.add(getDescriptorLabel("(" + stripped + ")", "#e3e3e3", 400, ""), 2, row);
			}
		} catch(PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		row += 1;

		centerGrid.add(getDescriptorLabel("Bit Depth: ", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + wave.getQuantizationBits(), "#e3e3e3", 400, ""), 2, row);
		row += 1;

		centerGrid.add(getDescriptorLabel("Locked: ", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + wave.getLocked(), "#e3e3e3", 400, ""), 2, row);
		row += 1;

		centerGrid.add(getDescriptorLabel("Audio Reference Level: ", "#e3e3e3", 200, ""), 1, row);
		try {
			byte audioRefLevel = wave.getAudioReferenceLevel();
			centerGrid.add(getDescriptorLabel("" + audioRefLevel, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;

		centerGrid.add(getDescriptorLabel("Electro-Spatial Formulation: ", "#e3e3e3", 200, ""), 1, row);
		try {
			ElectroSpatialFormulation electroForm = wave.getElectrospatialFormulation();
			centerGrid.add(getDescriptorLabel("" + electroForm, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;
		
		centerGrid.add(getDescriptorLabel("Dial Norm: ", "#e3e3e3", 200, "descriptor-border-bottom"), 1, row);
		try {
			byte dialNorm = wave.getDialNorm();
			centerGrid.add(getDescriptorLabel("" + dialNorm, "#e3e3e3", 400, "descriptor-border-bottom"), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, "descriptor-border-bottom"), 2, row);
		}
		row += 1;

		// WAVE Descriptors Section
		centerGrid.add(getDescriptorLabel("WAVE Descriptors", "#e3e3e3", 200, ""), 0, row);
		
		centerGrid.add(getDescriptorLabel("Average Bytes Per Second: ", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + wave.getAverageBytesPerSecond(), "#e3e3e3", 400, ""), 2, row);
		row += 1;

		centerGrid.add(getDescriptorLabel("Block Align: ", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + wave.getBlockAlign(), "#e3e3e3", 400, ""), 2, row);

		bp.setCenter(centerGrid);
		return bp;
	}

	private BorderPane createDateTimeCard(AS07DateTimeDescriptorImpl dateTime, int index) {
		BorderPane bp = new BorderPane();
		bp.setPadding(new Insets(10,0,10,0));
		GridPane topGrid = new GridPane();
		topGrid.setPadding(new Insets(10,10,10,10));
		Label label = new Label("AS07 DateTime Descriptor");
		label.setStyle("-fx-font-weight: bold;");
		topGrid.add(label, 0, 0);
		bp.setTop(topGrid);

		GridPane centerGrid = new GridPane();
		centerGrid.setPadding(new Insets(0,10,10,10));
		ColumnConstraints cc = new ColumnConstraints();
		cc.setPrefWidth(200);
		centerGrid.getColumnConstraints().add(cc);
		int row = 0;
		
		// File Descriptor Section
		centerGrid.add(getDescriptorLabel("File Descriptors", "#e3e3e3", 200, ""), 0, row);

		centerGrid.add(getDescriptorLabel("Instance UID", "#e3e3e3", 200, ""), 1, row);
		try {
			AUID originalAUID = dateTime.getOriginalAUID();
			centerGrid.add(getDescriptorLabel("" + originalAUID, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;
		
		centerGrid.add(getDescriptorLabel("Generation UID", "#e3e3e3", 200, ""), 1, row);
		try {
			AUID linkedGenerationID = dateTime.getLinkedGenerationID();
			centerGrid.add(getDescriptorLabel("" + linkedGenerationID, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;

		centerGrid.add(getDescriptorLabel("Linked Track ID", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + dateTime.getLinkedTrackID(), "#e3e3e3", 400, ""), 2, row);
		row += 1;

		centerGrid.add(getDescriptorLabel("Sample Rate", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + dateTime.getSampleRateString(), "#e3e3e3", 400, ""), 2, row);
		row += 1;
		
		centerGrid.add(getDescriptorLabel("Container Duration", "#e3e3e3", 200, ""), 1, row);
		try {
			long essenceLength = dateTime.getEssenceLength();
			centerGrid.add(getDescriptorLabel("" + essenceLength, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;

		centerGrid.add(getDescriptorLabel("Essence Container", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + dateTime.getEssenceContainer(), "#e3e3e3", 400, ""), 2, row);
		row += 1;

		centerGrid.add(getDescriptorLabel("Codec", "#e3e3e3", 200, ""), 1, row);
		try {
			ContainerDefinition containerFormat = dateTime.getContainerFormat();
			centerGrid.add(getDescriptorLabel("" + containerFormat, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;

		// TODO: iterate locators
		centerGrid.add(getDescriptorLabel("Locators", "#e3e3e3", 200, "descriptor-border-bottom"), 1, row);
		try {
			List<Locator> locators = dateTime.getLocators();
			centerGrid.add(getDescriptorLabel("" + locators, "#e3e3e3", 400, "descriptor-border-bottom"), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, "descriptor-border-bottom"), 2, row);
		}
		row += 1;

		// DateTime Descriptor Section
		centerGrid.add(getDescriptorLabel("DateTime Descriptors", "#e3e3e3", 200, ""), 0, row);

		centerGrid.add(getDescriptorLabel("DateTime Rate: ", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + dateTime.getDateTimeRate(), "#e3e3e3", 400, ""), 2, row);
		row += 1;

		centerGrid.add(getDescriptorLabel("DateTime Drop Frame: ", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + dateTime.getDateTimeDropFrame(), "#e3e3e3", 400, ""), 2, row);
		row += 1;

		centerGrid.add(getDescriptorLabel("DateTime Embedded: ", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + dateTime.getDateTimeEmbedded(), "#e3e3e3", 400, ""), 2, row);
		row += 1;

		centerGrid.add(getDescriptorLabel("DateTime Kind: ", "#e3e3e3", 200, "descriptor-border-bottom"), 1, row);
		centerGrid.add(getDescriptorLabel("" + dateTime.getDateTimeKind(), "#e3e3e3", 400, "descriptor-border-bottom"), 2, row);
		row += 1;

		// DateTime Descriptor Section
		if (dateTime.getSubDescriptors().size() > 0) {
			for (SubDescriptor sub : dateTime.getSubDescriptors()) {
				centerGrid.add(getDescriptorLabel("Subdescriptor", "#e3e3e3", 200, ""), 0, row);
				AS07TimecodeLabelSubdescriptor parsedSub = parseDateTimeSubDescriptor(sub.toString());

				centerGrid.add(getDescriptorLabel("DateTime Symbol: ", "#e3e3e3", 200, ""), 1, row);
				centerGrid.add(getDescriptorLabel("" + parsedSub.getDateTimeSymbol(), "#e3e3e3", 400, ""), 2, row);
				row += 1;
				
				centerGrid.add(getDescriptorLabel("DateTime Essence Track ID: ", "#e3e3e3", 200, "descriptor-border-bottom"), 1, row);
				centerGrid.add(getDescriptorLabel("" + parsedSub.getDateTimeEssenceTrackID(), "#e3e3e3", 400, "descriptor-border-bottom"), 2, row);
				row += 1;
			}
		}

		bp.setCenter(centerGrid);
		return bp;
	}

	private BorderPane createAncillaryCard(AncillaryPacketsDescriptorImpl ancillary, int index) {
		BorderPane bp = new BorderPane();
		bp.setPadding(new Insets(10,0,10,0));
		GridPane topGrid = new GridPane();
		topGrid.setPadding(new Insets(10,10,10,10));
		Label label = new Label("Ancillary Packets Descriptor");
		label.setStyle("-fx-font-weight: bold;");
		topGrid.add(label, 0, 0);
		bp.setTop(topGrid);

		GridPane centerGrid = new GridPane();
		centerGrid.setPadding(new Insets(0,10,10,10));
		ColumnConstraints cc = new ColumnConstraints();
		cc.setPrefWidth(200);
		centerGrid.getColumnConstraints().add(cc);
		int row = 0;
		
		// File Descriptors Section
		centerGrid.add(getDescriptorLabel("File Descriptors", "#e3e3e3", 200, ""), 0, row);
		
		centerGrid.add(getDescriptorLabel("Instance UID: ", "#e3e3e3", 200, ""), 1, row);
		try {
			AUID originalAUID = ancillary.getOriginalAUID();
			centerGrid.add(getDescriptorLabel("" + originalAUID, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;

		centerGrid.add(getDescriptorLabel("Generation UID: ", "#e3e3e3", 200, ""), 1, row);
		try {
			AUID generationID = ancillary.getLinkedGenerationID();
			centerGrid.add(getDescriptorLabel("" + generationID, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;
		
		centerGrid.add(getDescriptorLabel("Linked Track ID: ", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + ancillary.getLinkedTrackID(), "#e3e3e3", 400, ""), 2, row);
		row += 1;
		
		centerGrid.add(getDescriptorLabel("Sample Rate: ", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + ancillary.getSampleRate(), "#e3e3e3", 400, ""), 2, row);
		row += 1;
		
		centerGrid.add(getDescriptorLabel("Container Duration: ", "#e3e3e3", 200, ""), 1, row);
		try {
			long essenceLength = ancillary.getEssenceLength();
			centerGrid.add(getDescriptorLabel("" + essenceLength, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;
		
//		centerGrid.add(getDescriptorLabel("Essence Container: ", "#e3e3e3", 200, ""), 1, row);
//		centerGrid.add(getDescriptorLabel("" + ancillary.getContainerFormat(), "#e3e3e3", 400, ""), 2, row); // essence container??
//		row += 1;

		centerGrid.add(getDescriptorLabel("Codec: ", "#e3e3e3", 200, "descriptor-border-bottom"), 1, row);
		try {
			CodecDefinition codec = ancillary.getCodec();
			ContainerDefinition containerFormat = ancillary.getContainerFormat(); // ??
			centerGrid.add(getDescriptorLabel("" + codec, "#e3e3e3", 400, "descriptor-border-bottom"), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, "descriptor-border-bottom"), 2, row);
		}
		row += 1;
		
		// Ancillary Descriptors Section
		centerGrid.add(getDescriptorLabel("Generic Data Essence Descriptor", "#e3e3e3", 200, ""), 0, row);
		
		centerGrid.add(getDescriptorLabel("Data Essence Coding: ", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
//		centerGrid.add(getDescriptorLabel("" + ancillary.???, "#e3e3e3", 400, ""), 2, row); // data essence coding ??
		row += 1;

		bp.setCenter(centerGrid);
		return bp;
	}
	
	private BorderPane createTimedTextCard(TimedTextDescriptorImpl timedText, int index) {
		BorderPane bp = new BorderPane();
		bp.setPadding(new Insets(10,0,10,0));
		GridPane topGrid = new GridPane();
		topGrid.setPadding(new Insets(10,10,10,10));
		Label label = new Label("Timed Text Descriptor");
		label.setStyle("-fx-font-weight: bold;");
		topGrid.add(label, 0, 0);
		bp.setTop(topGrid);

		GridPane centerGrid = new GridPane();
		centerGrid.setPadding(new Insets(0,10,10,10));
		ColumnConstraints cc = new ColumnConstraints();
		cc.setPrefWidth(200);
		centerGrid.getColumnConstraints().add(cc);
		int row = 0;
		
		// File Descriptors Section
		centerGrid.add(getDescriptorLabel("File Descriptors", "#e3e3e3", 200, ""), 0, row);
		

		centerGrid.add(getDescriptorLabel("Instance UID: ", "#e3e3e3", 200, ""), 1, row);
		try {
			AUID originalAUID = timedText.getOriginalAUID();
			centerGrid.add(getDescriptorLabel("" + originalAUID, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;
		
		centerGrid.add(getDescriptorLabel("Generation UID: ", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + timedText.getLinkedGenerationID(), "#e3e3e3", 400, ""), 2, row);
		row += 1;
		
		centerGrid.add(getDescriptorLabel("Linked Track ID: ", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + timedText.getLinkedTrackID(), "#e3e3e3", 400, ""), 2, row);
		row += 1;

		centerGrid.add(getDescriptorLabel("Sample Rate: ", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + timedText.getSampleRate(), "#e3e3e3", 400, ""), 2, row);
		row += 1;

		centerGrid.add(getDescriptorLabel("Container Duration: ", "#e3e3e3", 200, ""), 1, row);
		centerGrid.add(getDescriptorLabel("" + timedText.getEssenceLength(), "#e3e3e3", 400, ""), 2, row);
		row += 1;
		
		// missing EssenceContainer?

		centerGrid.add(getDescriptorLabel("Codec: ", "#e3e3e3", 200, ""), 1, row);
		try {
			ContainerDefinition containerFormat = timedText.getContainerFormat();
			centerGrid.add(getDescriptorLabel("" + containerFormat.getDescription(), "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;

		// need to iterate locators array
		centerGrid.add(getDescriptorLabel("Locators: ", "#e3e3e3", 200, "descriptor-border-bottom"), 1, row);
		try {
			List<Locator> locators = timedText.getLocators();
			centerGrid.add(getDescriptorLabel("" + locators, "#e3e3e3", 400, "descriptor-border-bottom"), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, "descriptor-border-bottom"), 2, row);
		}
		row += 1;


		// Timed Text Descriptors Section
		centerGrid.add(getDescriptorLabel("Timed Text Descriptors", "#e3e3e3", 200, ""), 0, row);

		centerGrid.add(getDescriptorLabel("Text Encoding Format: ", "#e3e3e3", 200, ""), 1, row);
		try {
			String ucsEncoding = timedText.getUcsEncoding();
			centerGrid.add(getDescriptorLabel("" + ucsEncoding, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;

		centerGrid.add(getDescriptorLabel("Namespace URI: ", "#e3e3e3", 200, ""), 1, row);
		try {
			String namespaceURI = timedText.getNamespaceURI();
			centerGrid.add(getDescriptorLabel("" + namespaceURI, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;

		centerGrid.add(getDescriptorLabel("Resource ID: ", "#e3e3e3", 200, ""), 1, row);
		try {
			AUID resourceID = timedText.getResourceId();
			centerGrid.add(getDescriptorLabel("" + resourceID, "#e3e3e3", 400, ""), 2, row);
		} catch (PropertyNotPresentException e) {
			centerGrid.add(getDescriptorLabel("PROPERTY NOT PRESENT", "#e3e3e3", 400, ""), 2, row);
		}
		row += 1;

		bp.setCenter(centerGrid);
		return bp;
	}

	private Label getDescriptorLabel(String title, String color, int width, String borderClass) {
		Label label = new Label(title);
		if (color != "") label.setStyle("-fx-background-color: " + color);
		if (borderClass != "") label.getStyleClass().add(borderClass);
		label.getStyleClass().add("descriptor-rounded");
		label.setPrefWidth(width);
		label.setPadding(new Insets(2,5,2,5));
		label.setTooltip(new Tooltip(title));
		return label;
	}

	private AS07TimecodeLabelSubdescriptor parseDateTimeSubDescriptor(String sub) {
		try {
			String d = sub.replace("aaf:", "");
			JAXBContext jaxbContext = JAXBContext.newInstance(AS07TimecodeLabelSubdescriptor.class);
		    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		    AS07TimecodeLabelSubdescriptor subdescriptor = (AS07TimecodeLabelSubdescriptor)jaxbUnmarshaller.unmarshal(new StringReader(d));
		    return subdescriptor;
		} catch (JAXBException e)  {
		    e.printStackTrace();
		}
		return null;
	}
}
