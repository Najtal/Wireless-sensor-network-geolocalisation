package app;

import gui.Frame;
import model.AnalyzeModel;
import model.RawModel;
import dataReader.MoteReader;
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

	/**
	 * Constructeur. Crée le modele et la vue, par défaut sur la page de connection
	 */
	public Pgm() {

		Log.logInfo("Program started all good");


		this.rawModel = RawModel.getInstance();
		this.analyzeModel = AnalyzeModel.getInstance();
		Log.logInfo("Init models ok");

		this.moteReader = new MoteReader();
		Log.logInfo("Init CLI reader ok");

		this.frame = new Frame();
		Log.logInfo("Frame gui init ok");
	}

}
