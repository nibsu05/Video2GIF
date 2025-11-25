package controller;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import utils.TaskCompletionListener;

@WebListener
public class ServerContextListener implements ServletContextListener {

    private TaskCompletionListener listener;
    private Thread listenerThread;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // 1. Khởi tạo và chạy TCP Listener Thread
        listener = new TaskCompletionListener();
        listenerThread = new Thread(listener, "Task-Completion-Listener");
        listenerThread.start();
        
        // 2. Lưu listener vào context để có thể dừng khi server shutdown
        sce.getServletContext().setAttribute("taskListener", listener);
        
        System.out.println("--- TASK COMPLETION LISTENER STARTED ON PORT 9998 ---");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Dừng TCP Listener Thread khi server tắt
        TaskCompletionListener listener = (TaskCompletionListener) sce.getServletContext().getAttribute("taskListener");
        if (listener != null) {
            listener.stop();
            try {
                listenerThread.join(5000); // Đợi tối đa 5 giây để Thread dừng
                System.out.println("--- TASK COMPLETION LISTENER STOPPED ---");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("--- TASK COMPLETION LISTENER STOP FAILED ---");
            }
        }
    }
}