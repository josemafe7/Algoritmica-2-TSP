/*EPD08-P
 */
package TSP_Problem;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

/**
 *
 * @author Daniel Barciela Rueda
 * @author José Manuel Fernández Labrador
 */
public class TSP_Problem {

    public static void main(String[] args) throws IOException, Exception {

        menu();

    }

    //LEER DE FICHERO INSTANCIAS TSP Y MATRIZ DISTANCIA
     public static double[][] cargarMatrizFichero(String ruta) {
        Scanner entrada = null;
        String linea;
        double[][] matrizCoordenadas = null;

        try {
            File f = new File("data/" + ruta); //escribir ruta para que funcione
            //creamos un Scanner para leer el fichero
            entrada = new Scanner(f);
            //mostramos el nombre del fichero
            System.out.println("Archivo: " + f.getName());
            int dimension = 0;

            while (entrada.hasNext()) { //mientras no se llegue al final del fichero
                linea = entrada.nextLine();  //se lee una linea
                if (linea.contains("DIMENSION")) {   //si la linea contiene el texto buscado se muestra por pantalla
                    Scanner p = new Scanner(linea);
                    while (!p.hasNextInt()) {
                        p.next();
                    }
                    dimension = p.nextInt();

                }
                if (linea.contains("NODE_COORD_SECTION")) {   //si la linea contiene el texto buscado se muestra por pantalla
                    double[][] m = new double[dimension][2];
                    for (int i = 0; i < dimension; i++) {
                        int fila = entrada.nextInt();
                        double x = Double.parseDouble(entrada.next());
                        double y = Double.parseDouble(entrada.next());

                        m[fila - 1][0] = x;
                        m[fila - 1][1] = y;
                    }
                    matrizCoordenadas = m;
                }

            }

        } catch (IOException e) {
            System.out.println(e.toString());
        } finally {
            if (entrada != null) {
                entrada.close();
            }
        }
        return matrizCoordenadas;
    }

   
    public static void imprimirRuta(int[] v) {
        System.out.println("Imprimimos la ruta calculada:");
        for (int i = 0; i < v.length; i++) {
            if (i == v.length - 1) {
                System.out.println((v[i] + 1));
            } else {
                System.out.print((v[i] + 1) + " - ");  //+1 Simplemente para que muestre ciudades empezando por el 1
            }
        }
        System.out.println("\n");
    }

    public static double calculaDistancia(double x1, double y1, double x2, double y2) {

        return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));

    }

    public static double[][] calculaMatrizDistancia(double[][] mo) {
        double[][] md = new double[mo.length][mo.length];
        for (int i = 0; i < mo.length; i++) {

            for (int j = i; j < mo.length; j++) {
                md[i][j] = calculaDistancia(mo[i][0], mo[i][1], mo[j][0], mo[j][1]);

            }
        }
        return md;

    }


    //BUSQUEDA LOCAL
    public static int[] busquedaLocal(double[][] matrizDistancia, int cp) throws Exception {
        int[] actual = new int[matrizDistancia.length];
        param_Voraz(matrizDistancia, actual);
        int[] mejorR = actual.clone();
        double mejorC = distanciaTotalVector(mejorR, matrizDistancia);
        for (int i = 0; i < cp; i++) {
            if (nextPermutation(actual)) {
                double posibleSol = distanciaTotalVector(actual, matrizDistancia);
                if (posibleSol < mejorC) {
                    mejorC = posibleSol;
                    mejorR = actual.clone();

                }
            }

        }
        return mejorR;
    }
    
    public static void param_Voraz(double[][] matrizDistancia, int[] s) throws Exception {

        int fil = 0; // Suponemos que empieza en la primera ciudad
        s[0] = fil;
        for (int ciudad = 1; ciudad < s.length; ciudad++) {
            s[ciudad] = aVoraz(matrizDistancia, s, fil);
            fil = s[ciudad];
        }

    }
    
    public static int aVoraz(double[][] m, int[] s, int i) throws Exception {
        double min;
        int mejorCiudad = -1;

        min = Double.MAX_VALUE;

        for (int tarea = 0; tarea < m.length; tarea++) {

            if (!yaEscogida(s, tarea) && m[i][tarea] < min && i != tarea) {
                min = m[i][tarea];
                mejorCiudad = tarea;
            }

        }
        return mejorCiudad;
    }

    public static boolean yaEscogida(int[] x, int tarea) throws Exception {
        int i = 0;
        boolean escogido = false;

        while (i < x.length && !escogido) {
            if (x[i++] == tarea) {
                escogido = true;
            }
        }

        return escogido;
    }
    
    public static boolean nextPermutation(int[] array) {

        int i = array.length - 1;
        while (i > 0 && array[i - 1] >= array[i]) {
            i--;
        }

        if (i <= 0) {
            return false;
        }

        int j = array.length - 1;
        while (array[j] <= array[i - 1]) {
            j--;
        }

        int temp = array[i - 1];
        array[i - 1] = array[j];
        array[j] = temp;

        j = array.length - 1;
        while (i < j) {
            temp = array[i];
            array[i] = array[j];
            array[j] = temp;
            i++;
            j--;
        }
        return true;
    }

    public static void runExperimentBusquedaLocal(String ruta, int[] n, int[] ntimes) throws Exception {
        double[][] matrizCoordenadas = cargarMatrizFichero(ruta);
        double[][] matrizDistancia = calculaMatrizDistancia(matrizCoordenadas);

        double[] tIt = new double[n.length];
        double[] dIt = new double[n.length];
        int[] v = null;

        for (int i = 0; i < n.length; i++) {
            int d = n[i];

            long start = System.currentTimeMillis();
            for (int j = 0; j < ntimes[i]; j++) {
                v = busquedaLocal(matrizDistancia, d);
            }
            long stop = System.currentTimeMillis();

            tIt[i] = ((double) (stop - start)) / ((double) ntimes[i]);
            dIt[i] = distanciaTotalVector(v, matrizDistancia);
        }

        System.out.println("\nTiempo de ejecucion BUSQUEDA LOCAL: " + Arrays.toString(tIt) + " (ms) ");
        System.out.println("\nDistancias totales BUSQUEDA LOCAL: " + Arrays.toString(dIt) + " \n");
        System.out.println("RUTA POR BUSQUEDA LOCAL:");
        imprimirRuta(v);

    }

    //DIVIDE Y VENCERÁS
    
    public static int[] DyV(int[] ciudades, int inicio, int fin, double[][] dist) {
        int medio;
        int[] ruta;
        if (inicio < fin) {
            medio = (fin + inicio) / 2;
            DyV(ciudades, inicio, medio, dist);
            DyV(ciudades, (medio + 1), fin, dist);
            ruta = combina(ciudades, inicio, (medio + 1), fin, dist);
        } else {
            ruta = ciudades;
        }

        return ruta;
    }

    private static int[] combina(int[] ciudades, int inicio, int medio, int fin, double[][] matrizDistancia) {
        int[] aux = new int[ciudades.length];
        int medioIzq, aux_pos;
        medioIzq = (medio - 1);
        aux_pos = inicio;
        while ((inicio <= medioIzq) && (medio <= fin)) {
            if (coste(0, ciudades[inicio], matrizDistancia) <= coste(0, ciudades[medio], matrizDistancia)) {
                aux[aux_pos++] = ciudades[inicio++];
            } else {
                aux[aux_pos++] = ciudades[medio++];
            }
        }
        while (inicio <= medioIzq) {
            aux[aux_pos++] = ciudades[inicio++];
        }
        while (medio <= fin) {
            aux[aux_pos++] = ciudades[medio++];
        }

        return aux;
    }

    public static double coste(int origen, int destino, double[][] distanciaCiudades) {
        return distanciaCiudades[origen][destino];
    }

    public static int[] generaVectorCiudades(int numeroCiudades) {
        int[] v = new int[numeroCiudades];
        for (int i = 0; i < numeroCiudades; i++) {
            v[i] = i;
        }
        return v;
    }

    public static void runExperimentDivideYVenceras(String ruta, int ntimes) throws Exception {
        double[][] matrizCoordenadas = cargarMatrizFichero(ruta);
        double[][] matrizDistancia = calculaMatrizDistancia(matrizCoordenadas);

        int[] v = null;
        int[] ciudades = generaVectorCiudades(matrizDistancia.length);

        long start = System.currentTimeMillis();
        for (int i = 0; i < ntimes; i++) {
            v = DyV(ciudades, 0, ciudades.length - 1, matrizDistancia);
        }
        long stop = System.currentTimeMillis();

        long tiempo = (stop - start) / ntimes;

        System.out.println("\nTiempo de ejecucion DIVIDE Y VENCERAS: " + tiempo + " (ms)");
        System.out.println("\nDistancias totales DIVIDE Y VENCERAS: " + distanciaTotalVector(v, matrizDistancia) + " \n");

        System.out.println("RUTA POR DIVIDE Y VENCERAS:");
        imprimirRuta(v);

    }

   

    //VUELTA ATRÁS
    public static int[] param_vueltaAtras(double[][] m_Distancias, long ntimes, long contadorTiempo) {

        int nCiudades = m_Distancias.length;
        int[] rutaParcial = new int[nCiudades];
        int[] mejorRuta = new int[nCiudades];

        int costeMejorRuta = 0;
        int coste = 0;
        int nivel = 0;

        int[] v = vueltaAtras(m_Distancias, mejorRuta, rutaParcial, costeMejorRuta, coste, nivel, ntimes, contadorTiempo);
        return v;

    }

    public static int[] vueltaAtras(double[][] m_Distancias, int[] mejorRuta, int[] ruta, double costeMejorRuta, double coste, int nivel, long ntimes, long contadorTiempo) {
        if (System.currentTimeMillis() - contadorTiempo < ntimes) {

            boolean exito = false;

            int[] rutaParcial = new int[nivel + 1];
            copiarVector1(rutaParcial, ruta);
            int[] ciudadesProbadas = new int[m_Distancias.length];
            ciudadesProbadas(ciudadesProbadas);
            int Nciudad = nuevaCiudad(rutaParcial, ciudadesProbadas);

            while (Nciudad != -1 && !exito) {
                rutaParcial[nivel] = Nciudad;
                disminuirVector(rutaParcial);
                disminuirVector(mejorRuta);
                coste = distanciaTotalVector(rutaParcial, m_Distancias);
                costeMejorRuta = distanciaTotalVector(mejorRuta, m_Distancias);
                aumentarVector(rutaParcial);
                aumentarVector(mejorRuta);

                if (rutaParcial.length == ruta.length && coste < costeMejorRuta) {
                    mejorRuta = rutaParcial;
                    costeMejorRuta = coste;

                }

                if (SolucionParcialValida(coste, costeMejorRuta, nivel, ruta)) {

                    copiarVector2(rutaParcial, ruta);
                    nivel++;
                    mejorRuta = vueltaAtras(m_Distancias, mejorRuta, ruta, costeMejorRuta, coste, nivel, ntimes, contadorTiempo);
                    nivel--;

                }

                Nciudad = nuevaCiudad(rutaParcial, ciudadesProbadas);

            }
            if (costeMejorRuta == 0) {
                return rutaParcial;
            } else {
                return mejorRuta;
            }
        } else {
            return mejorRuta;
        }

    }

    public static boolean SolucionParcialValida(double costeRuta, double costeMejorRuta, int nivel, int[] ruta) {
        if (costeMejorRuta == 0 && nivel < ruta.length - 1) {
            return true;
        } else {
            return (costeRuta < costeMejorRuta && nivel < ruta.length - 1);
        }
    }

    public static int nuevaCiudad(int[] ruta, int[] ciudadesProbadas) {
        int numero = 0;
        boolean exito = false;
        for (int i = 0; i < ruta.length; i++) {
            for (int j = 0; j < ciudadesProbadas.length; j++) {
                if (ruta[i] == ciudadesProbadas[j]) {
                    ciudadesProbadas[j] = 0;
                }
            }
        }
        for (int i = 0; i < ciudadesProbadas.length && !exito; i++) {
            if (ciudadesProbadas[i] != 0) {
                exito = true;
                numero = ciudadesProbadas[i];
                ciudadesProbadas[i] = 0;
            }

        }

        if (numero == 0) {
            return -1;
        } else {
            return numero;
        }

    }

    public static void copiarVector1(int[] v1, int[] v2) {

        for (int i = 0; i < v1.length; i++) {
            v1[i] = v2[i];
        }
        v1[v1.length - 1] = 0;

    }

    public static void copiarVector2(int[] v1, int[] v2) {

        for (int i = 0; i < v1.length; i++) {
            v2[i] = v1[i];
        }

    }

    public static void disminuirVector(int[] v1) {

        for (int i = 0; i < v1.length; i++) {
            int n = v1[i];
            v1[i] = n - 1;
        }

    }

    public static void aumentarVector(int[] v1) {

        for (int i = 0; i < v1.length; i++) {
            int n = v1[i];
            v1[i] = n + 1;
        }

    }

    public static void ciudadesProbadas(int[] v) {
        for (int i = 0; i < v.length; i++) {
            v[i] = i + 1;
        }
    }

    public static double distanciaTotalVector(int[] v, double[][] m) {
        double res = 0;
        int fil;
        int col;

        for (int i = 0; i < v.length - 1; i++) { //Si se pone -1 no calcularia la vuelta a la primera ciudad, en este caso la calculo
            if (i == v.length - 1) {
                fil = v[0];
                col = v[i];
            } else {
                fil = v[i];
                col = v[i + 1];
            }
            if (fil < col) {
                res = res + m[fil][col];
            } else if (fil > col) {
                res = res + m[col][fil];
            }
        }

        return res;
    }

    public static void runExperimentVueltaAtras(String archivo, long ntimes, long contadorTiempo) {
        double[][] matrizCoordenadas = cargarMatrizFichero(archivo);
        double[][] matrizDistancia = calculaMatrizDistancia(matrizCoordenadas);

        int[] v = null;

        long start = System.currentTimeMillis();

        v = param_vueltaAtras(matrizDistancia, ntimes, contadorTiempo);

        long stop = System.currentTimeMillis();

        long tiempo = (stop - start);
        System.out.println("\nTiempo de ejecucion VUELTA ATRAS: " + tiempo + " (ms)");
        disminuirVector(v);
        System.out.println("\nDistancias totales VUELTA ATRAS: " + distanciaTotalVector(v, matrizDistancia) + " \n");

        System.out.println("RUTA POR VUELTA ATRAS:");
        imprimirRuta(v);

    }
    

    
    public static void menu() throws Exception {
        String[] ruta = {"berlin52.tsp", "kroA100.tsp", "kroA150.tsp", "kroA200.tsp", "a280.tsp", "vm1084.tsp", "vm1748.tsp", "usa13509.tsp"};

        for (int k = 0; k < ruta.length; k++) {
            runExperimentBusquedaLocal(ruta[k], new int[]{20, 25, 30, 35, 40}, new int[]{40, 35, 30, 25, 20});
            runExperimentDivideYVenceras(ruta[k], 100);
            long contadorTiempo = System.currentTimeMillis();
            runExperimentVueltaAtras(ruta[k], (long) 1500F, contadorTiempo);
        }
    }

}
