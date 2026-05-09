package com.mycompany.mavenproject3;

import java.util.*;

public class ConsoleApp {

    private static Control control = new Control();
    private static DataStore dataStore = new DataStore(control);
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("  SISTEMA DE INTERCAMBIO ESTUDIANTIL (CONSOLA)");
        System.out.println("==============================================");
        try {
            dataStore.load();
            System.out.println("Datos cargados: " + control.getEstudiantes().size() + " estudiantes, " + control.getConvenios().size() + " convenios.");
        } catch (Exception e) {
            System.out.println("No se encontraron datos, creando iniciales.");
            control.datos();
            try { dataStore.save(); } catch (Exception ex) {}
        }

        boolean salir = false;
        while (!salir) {
            System.out.println("\n--- MENU PRINCIPAL ---");
            System.out.println("1. Registrar Estudiante");
            System.out.println("2. Listar Estudiantes");
            System.out.println("3. Registrar Convenio");
            System.out.println("4. Listar Convenios");
            System.out.println("5. Crear Tramite");
            System.out.println("6. Listar Tramites");
            System.out.println("7. Subir/Eliminar Documento");
            System.out.println("8. Buscar Global");
            System.out.println("0. Salir");
            System.out.print("Opcion: ");
            int opcion = leerEntero();
            System.out.println();
            switch (opcion) {
                case 1: registrarEstudiante(); break;
                case 2: listarEstudiantes(); break;
                case 3: registrarConvenio(); break;
                case 4: listarConvenios(); break;
                case 5: crearTramite(); break;
                case 6: listarTramites(); break;
                case 7: gestionarDocumentos(); break;
                case 8: buscarGlobal(); break;
                case 0: salir = true; break;
                default: System.out.println("Opcion invalida");
            }
        }
        System.out.println("Guardando y saliendo...");
        try { dataStore.save(); } catch (Exception e) {}
        scanner.close();
    }

    private static int leerEntero() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.print("Debe ingresar un número: ");
            }
        }
    }

    private static String leerLinea(String mensaje) {
        System.out.println(mensaje);  // Usamos println para que se muestre siempre
        System.out.print("> ");
        return scanner.nextLine().trim();
    }

    private static void registrarEstudiante() {
        System.out.println("--- REGISTRAR ESTUDIANTE ---");
        String rut = leerLinea("RUT (formato 12.345.678-9):");
        if (!validarRUT(rut)) {
            System.out.println("RUT invalido.");
            return;
        }
        if (control.buscarEstudiante(rut) != null) {
            System.out.println("Ya existe un estudiante con ese RUT.");
            return;
        }
        String nombre = leerLinea("Nombre completo:");
        String carrera = leerLinea("Carrera:");
        int anio = Integer.parseInt(leerLinea("Año de ingreso:"));
        control.registrarEstudiante(rut, nombre, carrera, anio);
        System.out.println("Estudiante registrado exitosamente.");
        guardar();
    }

    private static void listarEstudiantes() {
        System.out.println("--- LISTA DE ESTUDIANTES ---");
        var estudiantes = control.getEstudiantes();
        if (estudiantes.isEmpty()) {
            System.out.println("No hay estudiantes registrados.");
        } else {
            for (Estudiante e : estudiantes) {
                System.out.println(e.getRut() + " - " + e.getNombre() + " - " + e.getCarrera());
            }
        }
    }

    private static void registrarConvenio() {
        System.out.println("--- REGISTRAR CONVENIO ---");
        String id = leerLinea("ID del convenio (ej: X-2026):");
        if (control.buscarConvenio(id) != null) {
            System.out.println("Ya existe un convenio con ese ID.");
            return;
        }
        String nombre = leerLinea("Nombre del convenio:");
        String universidad = leerLinea("Universidad socia:");
        String pais = leerLinea("País:");
        String duracion = leerLinea("Duración (ej: '6 meses'):");
        String carreraAsoc = leerLinea("Carrera asociada:");
        
        Set<TipoDocumento> requisitos = new HashSet<>();
        System.out.println("Requisitos (documentos necesarios):");
        for (TipoDocumento td : TipoDocumento.values()) {
            String resp = leerLinea("¿Agregar " + td + "? (s/n):").toLowerCase();
            if (resp.equals("s") || resp.equals("si")) {
                requisitos.add(td);
            }
        }
        Convenio c = new Convenio(id, nombre, universidad, pais, requisitos, duracion, carreraAsoc);
        control.agregarConvenio(c);
        System.out.println("Convenio registrado.");
        guardar();
    }

    private static void listarConvenios() {
        System.out.println("--- LISTA DE CONVENIOS ---");
        for (Convenio c : control.getConvenios()) {
            System.out.println(c.getIdConvenio() + " - " + c.getNombre() + " (" + c.getPais() + ")");
        }
    }

    private static void crearTramite() {
        System.out.println("--- CREAR TRÁMITE ---");
        String idConv = leerLinea("ID del Convenio:");
        Convenio conv = control.buscarConvenio(idConv);
        if (conv == null) {
            System.out.println("Convenio no existe.");
            return;
        }
        String rut = leerLinea("RUT del estudiante:");
        Estudiante est = control.buscarEstudiante(rut);
        if (est == null) {
            System.out.println("Estudiante no existe.");
            return;
        }
        Tramite t = conv.crearTramite(est);
        System.out.println("Trámite creado con ID: " + t.getIdTramite());
        guardar();
    }

    private static void listarTramites() {
        System.out.println("--- LISTA DE TRÁMITES ---");
        boolean hay = false;
        for (Convenio c : control.getConvenios()) {
            if (!c.getTramites().isEmpty()) {
                hay = true;
                System.out.println("Convenio: " + c.getIdConvenio());
                for (Tramite t : c.getTramites()) {
                    System.out.println("  " + t.getIdTramite() + " - " + t.getEstudiante().getRut() + " - " + t.getEstado());
                }
            }
        }
        if (!hay) System.out.println("No hay trámites registrados.");
    }

    private static void gestionarDocumentos() {
        System.out.println("--- GESTIÓN DE DOCUMENTOS ---");
        String idConv = leerLinea("ID del Convenio:");
        Convenio conv = control.buscarConvenio(idConv);
        if (conv == null) return;
        String idTram = leerLinea("ID del Trámite:");
        Tramite tram = conv.getTramites().stream()
                .filter(t -> t.getIdTramite().equals(idTram))
                .findFirst().orElse(null);
        if (tram == null) {
            System.out.println("Trámite no existe.");
            return;
        }
        System.out.println("1. Subir documento");
        System.out.println("2. Eliminar documento");
        int op = leerEntero();
        if (op == 1) {
            System.out.println("Tipos disponibles: " + Arrays.toString(TipoDocumento.values()));
            String tipoStr = leerLinea("Tipo de documento:").toUpperCase();
            TipoDocumento tipo;
            try {
                tipo = TipoDocumento.valueOf(tipoStr);
            } catch (Exception e) {
                System.out.println("Tipo inválido");
                return;
            }
            String archivo = leerLinea("Nombre del archivo:");
            tram.subirDocumento(tipo, archivo);
            conv.validarYActualizarEstado(tram);
            System.out.println("Documento subido.");
        } else if (op == 2) {
            System.out.println("Documentos actuales: " + tram.getDocumentos().keySet());
            String tipoStr = leerLinea("Tipo a eliminar:").toUpperCase();
            TipoDocumento tipo;
            try {
                tipo = TipoDocumento.valueOf(tipoStr);
            } catch (Exception e) {
                System.out.println("Tipo inválido");
                return;
            }
            if (tram.eliminarDocumento(tipo)) {
                conv.validarYActualizarEstado(tram);
                System.out.println("Documento eliminado.");
            } else {
                System.out.println("No existe ese documento.");
            }
        }
        guardar();
    }

    private static void buscarGlobal() {
        String texto = leerLinea("Texto a buscar (nombre, RUT, ID, etc.):");
        List<String> resultados = control.buscarGlobal(texto);
        if (resultados.isEmpty()) {
            System.out.println("No se encontraron resultados.");
        } else {
            for (String r : resultados) System.out.println(r);
        }
    }

    private static boolean validarRUT(String rut) {
        if (rut == null || rut.isBlank()) return false;
        String patron = "^\\d{1,2}\\.?\\d{3}\\.?\\d{3}-[\\dkK]$";
        if (!rut.matches(patron)) return false;
        String rutLimpio = rut.replaceAll("[^\\dkK]", "");
        if (rutLimpio.length() < 2) return false;
        String cuerpo = rutLimpio.substring(0, rutLimpio.length() - 1);
        char dv = rutLimpio.charAt(rutLimpio.length() - 1);
        try {
            int suma = 0, mult = 2;
            for (int i = cuerpo.length() - 1; i >= 0; i--) {
                suma += (cuerpo.charAt(i) - '0') * mult;
                mult = mult == 7 ? 2 : mult + 1;
            }
            int resto = 11 - (suma % 11);
            char dvCalc = resto == 11 ? '0' : resto == 10 ? 'k' : (char) ('0' + resto);
            return Character.toLowerCase(dv) == Character.toLowerCase(dvCalc);
        } catch (Exception e) {
            return false;
        }
    }

    private static void guardar() {
        try {
            dataStore.save();
            System.out.println("Datos guardados.");
        } catch (Exception e) {
            System.out.println("Error al guardar: " + e.getMessage());
        }
    }
}