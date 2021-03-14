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
    public void startApp() throws InterruptedException { ctrl.run();}
    public void doSomething(String message) {System.out.println(message);}
}

class Controller {
    enum STATUS {START, STOP}
    STATUS status;
    View view;
    Model model;

    public Controller(View view, Model model) {
        this.view = view;
        this.view.setController(this);
        this.model = model;
        startController();
    }

    public void run() throws InterruptedException {
        int count = 0;
        long someTime = (long)1000;
        while (status == STATUS.START) {
            view.doSomething("Let's continue " + model.getName());
            count += 1;
            sleep(someTime);

            if (count == 5) {
                stopController();
                view.doSomething("Let's stop " + model.getName());
            }
        }
    }

    public void startController() {
        status = STATUS.START;
    }

    public void stopController() {
        status = STATUS.STOP;
    }
}

public class Main {
    static Model myModel;
    static View myView;
    static Controller myController;

    public static void main(String[] args) {
        myModel = new Model("Victor");
        myView = new View();
        myController = new Controller(myView, myModel);
        try {
            myView.startApp();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
