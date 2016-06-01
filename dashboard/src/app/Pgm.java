package app;

import dataAnalyzer.LinearLeastSquareHandler;
import gui.CustomTableModel;
import gui.Frame;
import gui.GuiModel;
import model.AnalyzeModel;
import model.AnchorModel;
import model.RawModel;
import dataReader.MoteReader;
import util.AnchorReader;
import util.Log;



/**
 * Classe app.Pgm : Main program, main controller
 * @author Jean-Vital Durieu
 * @version 0.01
 */
public class Pgm {

	private final MoteReader moteReader;
	private final Frame frame;
	private final RawModel rawModel;
	private final AnalyzeModel analyzeModel;
	private final AnchorModel anchorModel;
	//private final LinearLeastSquareHandler llsh;

	/**
	 * Constructeur. Crée le modele et la vue, par défaut sur la page de connection
	 */
	public Pgm() {

		Log.logInfo("Program starting...");

		// Initialize the models
		this.rawModel = RawModel.INSTANCE;
		this.analyzeModel = AnalyzeModel.INSTANCE;
		this.anchorModel = AnchorModel.INSTANCE;
		Log.logFine("Init models ok");

		// The gui : listen to the analyzed model, when new sequence data received from the initializer, update
		AnchorReader.loadAnchorsToModel(AnchorModel.INSTANCE);
		GuiModel gModel = new GuiModel(AnchorModel.INSTANCE);
		this.frame = new Frame(gModel);
		Log.logFine("Frame gui init ok");

		// Init the reader : read the CLI, create RSSI & fills up the RawModel
		this.moteReader = new MoteReader(rawModel);
		Log.logFine("Init CLI reader ok");

		/*
		// Init initializer : Is trigged by the RawModel, if sequence data filled,
		// 		It analyze the raw data to the Analyzed model
		this.llsh = LinearLeastSquareHandler.INSTANCE;
		Log.logFine("Init Linear Least Square Handler ok");
		*/

		// Set frame visible when everything is launched
		this.frame.setVisible(true);
	}

}
