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
       Thread hilo = new Thread(tarea, "hilo-trabajador");
       hilo.run();
       tarea.run();
        
        try{
            hilo.join();
            System.out.println("main: el trabajador ha terminado");
        }catch(InterruptedException e)
        {
            System.out.println(Thread.currentThread().getName()+" ha sido interrumpido");
            Thread.currentThread().interrupt();
        }
    }
}
