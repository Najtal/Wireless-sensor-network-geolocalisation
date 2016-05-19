package app;

import dataAnalyzer.LinearLeastSquareHandler;
import gui.swing.Frame;
import model.AnalyzeModel;
import model.AnchorModel;
import model.RawModel;
import dataReader.MoteReader;
import gui.jfx.FxGui;
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
	private final LinearLeastSquareHandler llsh;

	/**
	 * Constructeur. Crée le modele et la vue, par défaut sur la page de connection
	 */
	public Pgm() {

		Log.logInfo("Program started all good");

		// Initialize the models
		this.rawModel = RawModel.INSTANCE;
		this.analyzeModel = AnalyzeModel.INSTANCE;
		this.anchorModel = AnchorModel.INSTANCE;
		Log.logInfo("Init models ok");

		// Init the reader : read the CLI, create RSSI & fills up the RawModel
		this.moteReader = new MoteReader(rawModel);
		Log.logInfo("Init CLI reader ok");

		// Init initializer : Is trigged by the RawModel, if sequence data filled,
		// 		It analyze the raw data to the Analyzed model
		this.llsh = LinearLeastSquareHandler.INSTANCE;
		Log.logInfo("Init Linear Least Square Handler ok");

		// The gui : listen to the analyzed model, when new sequence data received from the initializer, update
		this.frame = new Frame();
		try {
			 FxGui.create();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.logInfo("Frame gui init ok");
	}

}
