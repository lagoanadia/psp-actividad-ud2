import java.util.Date;

public class ServicioClub {
    static final int FRANJAS = 6; // sábado, de 10:00 a 15:00
    static final String[] franja = new String[FRANJAS + 1]; // null = libre. SIN PROTEGER todavía
    static final long[] caduca = new long[FRANJAS + 1]; // cuándo expira cada reserva sin pagar
    static Thread limpiador;

    static void traza(String msg) {
        System.out.printf("%tT.%<tL [%s] %s%n", new Date(), Thread.currentThread().getName(), msg);
    }

    private static final Object CERROJO = new Object(); // la llave de la sección crítica

    /** Comprobar y actuar, protegidos con un cerrojo privado (versión A). */
    static boolean reservar(int id, String quien) {
        synchronized (CERROJO) {
            if (franja[id] == null) { // 1 · comprobar
                franja[id] = quien; // 2 · actuar
                caduca[id] = System.currentTimeMillis() + 900_000; // quince minutos para pagar
                return true;
            }
            return false;
        }
    }

    static void cancelar(int id) { synchronized (CERROJO) { franja[id] = null; } }

    /** Una vuelta entera de limpieza: libera lo caducado y no deja nada a medias. */
    static void limpiar() {
        traza("vuelta de limpieza: empiezo");
        for (int i = 1; i <= FRANJAS; i++) {
            String quien = franja[i];
            if (quien != null && System.currentTimeMillis() > caduca[i]) {
                franja[i] = null;
                traza("franja " + i + " liberada (era de " + quien + ")");
            }
            try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        }
        traza("vuelta de limpieza: termino sin dejar nada a medias");
    }

    public static void main(String[] args) throws Exception {
        franja[2] = "ana";   caduca[2] = System.currentTimeMillis() + 500;
        franja[5] = "bruno"; caduca[5] = System.currentTimeMillis() + 500;

        limpiador = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                limpiar();
                try { Thread.sleep(1000); }
                catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
            }
            traza("limpiador detenido");
        }, "limpiador");
        limpiador.setDaemon(true);
        limpiador.setPriority(Thread.MIN_PRIORITY);
        // ACTIVIDAD 4: gancho de cierre
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            traza("apagando: interrumpo el limpiador");
            limpiador.interrupt();
            try {
                limpiador.join(3000); // espera con límite: una vuelta tarda ~1,2 s
                traza(limpiador.isAlive() ? "NO terminó en 3 s" : "limpiador detenido y unido");
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
            traza("servicio apagado");
        }, "gancho-cierre"));
        limpiador.start();
        traza("servicio del club arrancado");
        Thread.sleep(args.length > 0 ? Long.parseLong(args[0]) : 3000);
        traza("main termina: se dispara el gancho de cierre");
    }
}
