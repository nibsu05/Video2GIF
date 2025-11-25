package controller;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import utils.TaskCompletionListener;
import utils.ApplicationConfig;

@WebListener
public class ServerContextListener implements ServletContextListener {

    private TaskCompletionListener listener;
    private Thread listenerThread;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ApplicationConfig.initializeStorage();
        
        listener = new TaskCompletionListener();
        listenerThread = new Thread(listener, "Task-Completion-Listener");
        listenerThread.start();
        
        sce.getServletContext().setAttribute("taskListener", listener);
        
        System.out.println("--- TASK COMPLETION LISTENER STARTED ON PORT " + ApplicationConfig.COMPLETION_LISTENER_PORT + " ---");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        TaskCompletionListener listener = (TaskCompletionListener) sce.getServletContext().getAttribute("taskListener");
        if (listener != null) {
            listener.stop();
            try {
                listenerThread.join(5000);
                System.out.println("--- TASK COMPLETION LISTENER STOPPED ---");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("--- TASK COMPLETION LISTENER STOP FAILED ---");
            }
        }
    }
}