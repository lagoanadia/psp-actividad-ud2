public class StartRun{
    public static void main(String[]args)
    {
        Runnable tarea = () -> {
            String yo = Thread.currentThread().getName();
            for(int i=1; i<=3;i++)
            {
                System.out.println(yo+" va por "+i);
                try 
                {
                    Thread.sleep(200);
                } catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt(); return;
                }
            }
        };
       Thread hilo1 = new Thread(tarea, "hilo1");
       Thread hilo2 = new Thread(tarea, "hilo2");
       Thread hilo3 = new Thread(tarea, "hilo3");
        
       hilo1.start();
       hilo2.start();
       hilo3.start();

        try{
            hilo1.join();
            hilo2.join();
            hilo3.join();
           System.out.println("main: las 3 tareas han terminado.");
        }catch(InterruptedException e)
        {
            System.out.println(Thread.currentThread().getName()+" ha sido interrumpido");
            Thread.currentThread().interrupt();
        }
    }
}
