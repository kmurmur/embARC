package com.portalmedia.embarc.gui.mxf;

import java.io.IOException;

import com.portalmedia.embarc.gui.model.MXFSelectedFilesSummary;
import com.portalmedia.embarc.parser.mxf.MXFFileInfo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

/**
 * File info pane view
 *
 * @author PortalMedia
 * @version 1.0
 * @since 2020-07-13
 */
public class FileInfoController extends AnchorPane {

	@FXML
	private Label fileInfoAreaLabel;
	@FXML
	private Label selectedFilesLabel;
	@FXML
	private AnchorPane fileInfoAnchorPane;

	int numSelected;

	public FileInfoController() {
		ControllerMediatorMXF.getInstance().registerFileInfoView(this);
		final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FileInfoView.fxml"));
		fxmlLoader.setController(this);
		fxmlLoader.setRoot(this);
		try {
			fxmlLoader.load();
		} catch (final IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	private void setNumberOfSelectedFiles(int num) {
		numSelected = num;
		if (num == 1) {
			selectedFilesLabel.setText(Integer.toString(num) + " file selected");
		} else {
			selectedFilesLabel.setText(Integer.toString(num) + " files selected");
		}
	}

	public void setContent() {
		fileInfoAreaLabel.setText("File Information");

		final MXFSelectedFilesSummary summary = ControllerMediatorMXF.getInstance().getSelectedFilesSummary();

		String fileName = summary.getFileName();
		String filePath = summary.getFilePath();
		String videoTrackCount = summary.getFileInfoDisplayValues(MXFFileInfo.VIDEO_TRACK_COUNT);
		String audioTrackCount = summary.getFileInfoDisplayValues(MXFFileInfo.AUDIO_TRACK_COUNT);
		String captionTrackCount = summary.getFileInfoDisplayValues(MXFFileInfo.CAPTION_TRACK_COUNT);
		String timecodeTrackCount = summary.getFileInfoDisplayValues(MXFFileInfo.TIMECODE_TRACK_COUNT);
		String tdCount = summary.getFileInfoDisplayValues(MXFFileInfo.TD_COUNT);
		String bdCount = summary.getFileInfoDisplayValues(MXFFileInfo.BD_COUNT);

		final GridPane grid = new GridPane();
		grid.setAlignment(Pos.TOP_CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));
//		ColumnConstraints column0 = new ColumnConstraints(160, 160, Double.MAX_VALUE);
//	    column0.setHgrow(Priority.SOMETIMES);
//		grid.getColumnConstraints().add(column0);
//		ColumnConstraints column1 = new ColumnConstraints(400, 400, Double.MAX_VALUE);
//	    column1.setHgrow(Priority.SOMETIMES);
//		grid.getColumnConstraints().add(column1);

		grid.add(new Label("Filename: "), 0, 0);
		Label fileNameLabel = new Label(fileName);
		fileNameLabel.setWrapText(true);
		grid.add(fileNameLabel, 1, 0);

		grid.add(new Label("File Path: "), 0, 1);
		Label filePathLabel = new Label(filePath);
		filePathLabel.setWrapText(true);
		grid.add(filePathLabel, 1, 1);

		grid.add(new Label("Format: "), 0, 2);
		grid.add(new Label(summary.getFileInfoDisplayValues(MXFFileInfo.FORMAT)), 1, 2);

		grid.add(new Label("Version: "), 0, 3);
		grid.add(new Label(summary.getFileInfoDisplayValues(MXFFileInfo.VERSION)), 1, 3);

		grid.add(new Label("Profile: "), 0, 4);
		Label profileLabel = new Label(summary.getFileInfoDisplayValues(MXFFileInfo.PROFILE));
		profileLabel.setWrapText(true);
		grid.add(profileLabel, 1, 4);

		grid.add(new Label("File Size: "), 0, 5);
		grid.add(new Label(summary.getFileInfoDisplayValues(MXFFileInfo.FILE_SIZE)), 1, 5);

		grid.add(new Label("Video Track Count: "), 0, 6);
		grid.add(new Label(videoTrackCount), 1, 6);

		grid.add(new Label("Audio Track Count: "), 0, 7);
		grid.add(new Label(audioTrackCount), 1, 7);

		grid.add(new Label("Caption Track Count: "), 0, 8);
		grid.add(new Label(captionTrackCount), 1, 8);

		grid.add(new Label("Timecode Track Count: "), 0, 9);
		grid.add(new Label(timecodeTrackCount), 1, 9);

		grid.add(new Label("TD Count: "), 0, 10);
		grid.add(new Label(tdCount), 1, 10);

		grid.add(new Label("BD Count: "), 0, 11);
		grid.add(new Label(bdCount), 1, 11);

		fileInfoAnchorPane.getChildren().add(grid);

		setNumberOfSelectedFiles(summary.getFileCount());
	}

}