import static java.lang.Thread.sleep;

class Model {
    String name;

    Model(String name) {this.name = name;}
    String getName() {return this.name;}
}

class View {
    Controller ctrl;

    public View() {}
    public void setController(Controller ctrl) {this.ctrl = ctrl;}
    public void stopApp() {ctrl.stopController();}
    public void doSomething(String message) {System.out.println(message);}
}

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
        startController();
        while (running()) {
            view.doSomething("Let's continue " + model.getName());
            try {
                sleep(ONE_SECOND);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        view.doSomething("Let's stop " + model.getName());
    }

    public void startController() {
        status = STATUS.STARTED;
    }

    public void stopController() {
        status = STATUS.STOPPED;
    }

    boolean running() {return status == STATUS.STARTED;}
}

public class Main {
    static Model myModel;
    static View myView;
    static Controller myController;

    public static void main(String[] args) {
        myModel = new Model("Victor");
        myView = new View();
        myController = new Controller(myView, myModel);
        myController.start();

        try {
            sleep(5000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        myView.stopApp();
    }
}
