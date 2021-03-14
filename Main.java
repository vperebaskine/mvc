import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import static java.lang.Thread.sleep;

/*
 * This class create a Logger and provide a method to associate a log file to this logger
 * To use this class, just call MyLogger.logger.setFileHandler(<File Name>)
 */
class MyLogger {
    private static final String LOGGER_NAME = MyLogger.class.getName();
    private static final String DEFAULT_LOGFILE_NAME = "mvc.log";
    protected static final Logger logger = Logger.getLogger(LOGGER_NAME);

    private MyLogger() {
        throw new IllegalStateException(LOGGER_NAME);
    }

    public static void setFileHandler(String fileName) throws SecurityException, IOException {
        final Handler fh;

        if (fileName == null) {
            fh = new FileHandler(DEFAULT_LOGFILE_NAME, true);
        } else {
            fh = new FileHandler(fileName, true);
        }
        logger.addHandler(fh);
    }
}

/*
 * Implement a basic MVC patern
 * @author : Victor Perebaskine
 * @date : March 14th 2021
 */

/*
 * The Model implements the data the Controlller is using
 */
class Model {
    String name;

    Model(String name) {this.name = name;}
    String getName() {return this.name;}
}

/*
 * The View manages the user interface.
 * The View interacts only with the Controller
 * Within the View, the method setController allows to set the Controller
 */
class View {
    Controller ctrl;

    public View() {}
    public void setController(Controller ctrl) {this.ctrl = ctrl;}
    public void stopApp() {ctrl.stopController();}
    public void doSomething(String message) {MyLogger.logger.log(Level.INFO, message);}
}

/*
 * The Controller will be executed in a dedicated thread
 * The constructor initialize the View and the Model
 * The View and the model are created elsewhere (ex. in the main function)
 * Once the View is set, the Controller complete the View initialisation by setting the Controller reference
 * The Controller includes a run() method, called by the Thread.start() method.
 * The Controller also has start() and stop() methods
 */
class Controller extends Thread {
    enum STATUS {STARTED, STOPPED}
    STATUS status;
    View view;
    Model model;

    public Controller(View view, Model model) {
        this.view = view;
        this.view.setController(this);
        this.model = model;
    }

    @Override
    public void run() {
        long ONE_SECOND = (long)1000;
        String PROCEED = "Let's Continue " + model.getName();
        String STOP = "Let's stop " + model.getName();

        startController();
        while (running()) {
            view.doSomething(PROCEED);
            try {
                sleep(ONE_SECOND);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        view.doSomething(STOP);
    }

    public void startController() {
        status = STATUS.STARTED;
    }

    public void stopController() {
        status = STATUS.STOPPED;
    }

    boolean running() {return status == STATUS.STARTED;}
}

/*
 * The main program creates the Model, the View and the Controller
 * and initialize the logger (in order not to write on the standard output)
 * Then it start the Controller in a separated Thread.
 * The last action is to simulate a user interaction with the View to stop the Controller and end the application.
 */
public class Main {
    static Model myModel;
    static View myView;
    static Controller myController;

    public static void main(String[] args) {
        myModel = new Model("Victor");
        myView = new View();
        myController = new Controller(myView, myModel);

        try {
            MyLogger.setFileHandler(null);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }

        myController.start();

        try {
            sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        myView.stopApp();
    }
}
