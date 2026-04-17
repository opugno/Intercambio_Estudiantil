package com.mycompany.mavenproject3;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.function.Consumer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Map;

/**
 *
 * @author sonailslove
 */
public class Main.java extends JFrame
{
    private Control herramientas;
    private JTabbedPane tabbedPane;
    
    // Paneles principales
    private JPanel panelEstudiantes;
    private JPanel panelTramites;
    private JPanel panelDocumentos;
    private JPanel panelConvenios;
    private JPanel panelRequisitos;
    private JPanel panelGestionConvenios;
    
    // Componentes reutilizables
    private JComboBox<String> comboConvenios;
    private JComboBox<String> comboEstudiantes;
    private DefaultTableModel modeloTablaConvenios;
    private DefaultTableModel modeloTablaTramites;
    
    public Main() 
    {
        herramientas = new Control();
        herramientas.datos(); // Cargar datos iniciales
        
        initComponents();
        actualizarCombos();
        actualizarTablas();
    }
    
    private void initComponents() {
        setTitle("Sistema de Gestión de Intercambio Estudiantil");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Crear el panel con pestañas
        tabbedPane = new JTabbedPane();
        
        // Crear cada pestaña
        crearPanelEstudiantes();
        crearPanelTramites();
        crearPanelDocumentos();
        crearPanelConvenios();
        crearPanelRequisitos();
        crearPanelGestionConvenios();
        
        // Agregar pestañas
        tabbedPane.addTab("Registrar Estudiante", panelEstudiantes);
        tabbedPane.addTab("Crear Trámite", panelTramites);
        tabbedPane.addTab("Subir Documentos", panelDocumentos);
        tabbedPane.addTab("Ver Convenios y Trámites", panelConvenios);
        tabbedPane.addTab("Configurar Requisitos", panelRequisitos);
        tabbedPane.addTab("Convenios", panelGestionConvenios);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Panel inferior con información
        JPanel panelInfo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelInfo.setBorder(BorderFactory.createEtchedBorder());
        JLabel lblInfo = new JLabel("Sistema de Intercambio v1.0");
        panelInfo.add(lblInfo);
        add(panelInfo, BorderLayout.SOUTH);
        
        // Configuración de la ventana
        setSize(900, 600);
        setLocationRelativeTo(null);
    }
    
    private void crearPanelEstudiantes() {
        panelEstudiantes = new JPanel(new GridBagLayout());
        panelEstudiantes.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Título
        JLabel titulo = new JLabel("Registrar Nuevo Estudiante");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        panelEstudiantes.add(titulo, gbc);
        
        // Separador
        gbc.gridy = 1;
        panelEstudiantes.add(new JSeparator(), gbc);
        
        // Campos del formulario
        gbc.gridwidth = 1;
        
        // RUT
        gbc.gridx = 0; gbc.gridy = 2;
        panelEstudiantes.add(new JLabel("RUT:"), gbc);
        
        JTextField txtRut = new JTextField(20);
        gbc.gridx = 1;
        panelEstudiantes.add(txtRut, gbc);
        
        // Nombre
        gbc.gridx = 0; gbc.gridy = 3;
        panelEstudiantes.add(new JLabel("Nombre:"), gbc);
        
        JTextField txtNombre = new JTextField(20);
        gbc.gridx = 1;
        panelEstudiantes.add(txtNombre, gbc);
        
        // Carrera
        gbc.gridx = 0; gbc.gridy = 4;
        panelEstudiantes.add(new JLabel("Carrera:"), gbc);
        
        JTextField txtCarrera = new JTextField(20);
        gbc.gridx = 1;
        panelEstudiantes.add(txtCarrera, gbc);
        
        // Año de ingreso
        gbc.gridx = 0; gbc.gridy = 5;
        panelEstudiantes.add(new JLabel("Año de Ingreso:"), gbc);
        
        JSpinner spinnerAnio = new JSpinner(new SpinnerNumberModel(2024, 2000, 2030, 1));
        gbc.gridx = 1;
        panelEstudiantes.add(spinnerAnio, gbc);
        
        // Botón registrar
        JButton btnRegistrar = new JButton("Registrar Estudiante");
        btnRegistrar.setBackground(new Color(0, 123, 255));
        btnRegistrar.setForeground(Color.WHITE);
        gbc.gridx = 1; gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.EAST;
        panelEstudiantes.add(btnRegistrar, gbc);
        
        // Área de mensajes
        JTextArea areaMensajes = new JTextArea(3, 40);
        areaMensajes.setEditable(false);
        areaMensajes.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        JScrollPane scrollMensajes = new JScrollPane(areaMensajes);
        gbc.gridx = 0; gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelEstudiantes.add(scrollMensajes, gbc);
        
        // Acción del botón
        btnRegistrar.addActionListener(e -> {
            String rut = txtRut.getText().trim();
            String nombre = txtNombre.getText().trim();
            String carrera = txtCarrera.getText().trim();
            int anio = (int) spinnerAnio.getValue();
            
            if (rut.isEmpty() || nombre.isEmpty() || carrera.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Por favor complete todos los campos", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (herramientas.buscarEstudiante(rut) != null) {
                JOptionPane.showMessageDialog(this, 
                    "Ya existe un estudiante con ese RUT", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            herramientas.registrarEstudiante(rut, nombre, carrera, anio);
            areaMensajes.setText("Estudiante registrado exitosamente:\n" +
                               "RUT: " + rut + "\n" +
                               "Nombre: " + nombre);
            
            // Limpiar campos
            txtRut.setText("");
            txtNombre.setText("");
            txtCarrera.setText("");
            spinnerAnio.setValue(2024);
            
            actualizarCombos();
            JOptionPane.showMessageDialog(this, 
                "Estudiante registrado con éxito", 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void crearPanelTramites() {
        panelTramites = new JPanel(new GridBagLayout());
        panelTramites.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Título
        JLabel titulo = new JLabel("Crear Trámite de Postulación");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        panelTramites.add(titulo, gbc);
        
        // Convenio
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panelTramites.add(new JLabel("Convenio:"), gbc);
        
        JComboBox<String> comboConvenioTramite = new JComboBox<>();
        gbc.gridx = 1;
        panelTramites.add(comboConvenioTramite, gbc);
        
        // Estudiante
        gbc.gridx = 0; gbc.gridy = 2;
        panelTramites.add(new JLabel("Estudiante (RUT):"), gbc);
        
        JComboBox<String> comboEstudianteTramite = new JComboBox<>();
        gbc.gridx = 1;
        panelTramites.add(comboEstudianteTramite, gbc);
        
        // Campo para ID manual
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        panelTramites.add(new JLabel("ID Trámite (opcional):"), gbc);

        JTextField txtIdManual = new JTextField(20);
        gbc.gridx = 1;
        panelTramites.add(txtIdManual, gbc);

        // Área de información
        JTextArea areaInfo = new JTextArea(10, 40);
        areaInfo.setEditable(false);
        areaInfo.setBorder(BorderFactory.createTitledBorder("Información del Trámite"));
        JScrollPane scrollInfo = new JScrollPane(areaInfo);
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panelTramites.add(scrollInfo, gbc);

        // Botón crear trámite
        JButton btnCrearTramite = new JButton("Crear Trámite");
        btnCrearTramite.setBackground(new Color(40, 167, 69));
        btnCrearTramite.setForeground(Color.WHITE);
        gbc.gridx = 1; gbc.gridy = 5;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.EAST;
        panelTramites.add(btnCrearTramite, gbc);

        
        // Actualizar combos
        actualizarComboConvenios(comboConvenioTramite);
        actualizarComboEstudiantes(comboEstudianteTramite);
        
        // Listener para actualizar combos cuando se cambia de pestaña
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedComponent() == panelTramites) {
                actualizarComboConvenios(comboConvenioTramite);
                actualizarComboEstudiantes(comboEstudianteTramite);
            }
        });
        
        // Acción del botón
        btnCrearTramite.addActionListener(e -> {
            String idConvenio = (String) comboConvenioTramite.getSelectedItem();
            String rut = (String) comboEstudianteTramite.getSelectedItem();
            String idManual = txtIdManual.getText().trim();

            if (idConvenio == null || rut == null) {
                JOptionPane.showMessageDialog(this, "Seleccione convenio y estudiante", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Convenio convenio = herramientas.buscarConvenio(idConvenio.split(" - ")[0]);
            Estudiante estudiante = herramientas.buscarEstudiante(rut);

            if (convenio == null || estudiante == null) {
                JOptionPane.showMessageDialog(this, "Datos no válidos", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Tramite tramite;

            if (!idManual.isEmpty()) {
                // Validar que no esté duplicado
                boolean existe = convenio.getTramites().stream()
                    .anyMatch(t -> t.getIdTramite().equals(idManual));

                if (existe) {
                    JOptionPane.showMessageDialog(this, "Ya existe un trámite con ese ID", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                tramite = new Tramite(idManual, estudiante, convenio);
                convenio.getTramites().add(tramite);
            } else {
                tramite = convenio.crearTramite(estudiante); // ID automático
            }

            // Mostrar información
            StringBuilder info = new StringBuilder();
            info.append("Trámite creado exitosamente!\n");
            info.append("============================\n");
            info.append("ID Trámite: ").append(tramite.getIdTramite()).append("\n");
            info.append("Estudiante: ").append(estudiante.getNombre()).append("\n");
            info.append("Convenio: ").append(convenio.getNombre()).append("\n");
            info.append("Universidad: ").append(convenio.getUniversidadSocia()).append("\n");
            info.append("País: ").append(convenio.getPais()).append("\n");
            info.append("\nRequisitos necesarios:\n");
            for (TipoDocumento req : convenio.getRequisitos()) {
                info.append(" • ").append(req.toString()).append("\n");
            }
            info.append("\nEstado: ").append(tramite.getEstado());

            areaInfo.setText(info.toString());
            actualizarTablas();
            JOptionPane.showMessageDialog(this, "Trámite creado con ID: " + tramite.getIdTramite(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
        });
    }

    private void crearPanelDocumentos() {
        panelDocumentos = new JPanel(new GridBagLayout());
        panelDocumentos.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Título
        JLabel titulo = new JLabel("Subir Documentos a Trámite");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2;
        panelDocumentos.add(titulo, gbc);
        
        // Convenio
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panelDocumentos.add(new JLabel("Convenio:"), gbc);
        
        JComboBox<String> comboConvenioDoc = new JComboBox<>();
        gbc.gridx = 1;
        panelDocumentos.add(comboConvenioDoc, gbc);
        
        // Trámite
        gbc.gridx = 0; gbc.gridy = 2;
        panelDocumentos.add(new JLabel("ID Trámite:"), gbc);
        
        JComboBox<String> comboTramite = new JComboBox<>();
        gbc.gridx = 1;
        panelDocumentos.add(comboTramite, gbc);
        
        // Tipo de documento
        gbc.gridx = 0; gbc.gridy = 3;
        panelDocumentos.add(new JLabel("Tipo de Documento:"), gbc);
        
        JComboBox<TipoDocumento> comboTipoDoc = new JComboBox<>(TipoDocumento.values());
        gbc.gridx = 1;
        panelDocumentos.add(comboTipoDoc, gbc);
        
        // Nombre del archivo
        gbc.gridx = 0; gbc.gridy = 4;
        panelDocumentos.add(new JLabel("Nombre del Archivo:"), gbc);
        
        JTextField txtNombreArchivo = new JTextField(20);
        gbc.gridx = 1;
        panelDocumentos.add(txtNombreArchivo, gbc);
        
        // Botón simular selección de archivo
        JButton btnSeleccionar = new JButton("Simular Selección");
        gbc.gridx = 2;
        panelDocumentos.add(btnSeleccionar, gbc);
        
        // Área de estado
        JTextArea areaEstado = new JTextArea(8, 40);
        areaEstado.setEditable(false);
        areaEstado.setBorder(BorderFactory.createTitledBorder("Estado del Trámite"));
        JScrollPane scrollEstado = new JScrollPane(areaEstado);
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        panelDocumentos.add(scrollEstado, gbc);
        
        // Botón subir documento
        JButton btnSubir = new JButton("Subir Documento");
        btnSubir.setBackground(new Color(255, 193, 7));
        btnSubir.setForeground(Color.BLACK);
        gbc.gridx = 1; gbc.gridy = 6;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        panelDocumentos.add(btnSubir, gbc);
        
        // Actualizar combos
        actualizarComboConvenios(comboConvenioDoc);
        
        // Listener para actualizar trámites cuando se selecciona un convenio
        comboConvenioDoc.addActionListener(e -> {
            actualizarComboTramites(comboConvenioDoc, comboTramite);
        });
        
        // Listener para actualizar estado cuando se selecciona un trámite
        comboTramite.addActionListener(e -> {
            actualizarEstadoTramite(comboConvenioDoc, comboTramite, areaEstado);
        });
        
        // Listener para cambio de pestaña
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedComponent() == panelDocumentos) {
                actualizarComboConvenios(comboConvenioDoc);
            }
        });
        
        // Simular selección de archivo
        btnSeleccionar.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                txtNombreArchivo.setText(fileChooser.getSelectedFile().getName());
            }
        });
        
        // Acción subir documento
        btnSubir.addActionListener(e -> {
            String idConvenio = (String) comboConvenioDoc.getSelectedItem();
            String idTramite = (String) comboTramite.getSelectedItem();
            TipoDocumento tipo = (TipoDocumento) comboTipoDoc.getSelectedItem();
            String nombreArchivo = txtNombreArchivo.getText().trim();
            
            if (idConvenio == null || idTramite == null || nombreArchivo.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Complete todos los campos", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Convenio convenio = herramientas.buscarConvenio(idConvenio.split(" - ")[0]);
            if (convenio == null) return;
            
            Tramite tramite = convenio.getTramites().stream()
                .filter(t -> t.getIdTramite().equals(idTramite))
                .findFirst()
                .orElse(null);
            
            if (tramite == null) {
                JOptionPane.showMessageDialog(this, 
                    "Trámite no encontrado", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            tramite.subirDocumento(tipo, nombreArchivo);
            convenio.validarYActualizarEstado(tramite);
            
            actualizarEstadoTramite(comboConvenioDoc, comboTramite, areaEstado);
            actualizarTablas();
            
            JOptionPane.showMessageDialog(this, 
                "Documento subido exitosamente", 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);
            
            txtNombreArchivo.setText("");
        });
        
        JButton btnEliminarDoc = new JButton("Eliminar Documento");
        btnEliminarDoc.addActionListener(e -> {
            String idConvenio = (String) comboConvenioDoc.getSelectedItem();
            String idTramite = (String) comboTramite.getSelectedItem();
            TipoDocumento tipo = (TipoDocumento) comboTipoDoc.getSelectedItem();

            if (idConvenio == null || idTramite == null || tipo == null) {
                JOptionPane.showMessageDialog(this, "Selecciona convenio, trámite y tipo de documento.");
                return;
            }

            String idConvenioReal = idConvenio.contains(" - ") ? idConvenio.split(" - ")[0] : idConvenio;

            boolean ok = herramientas.eliminarDocumentoDeTramite(idConvenioReal, idTramite, tipo);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Documento eliminado.");
                actualizarEstadoTramite(comboConvenioDoc, comboTramite, areaEstado);
                actualizarTablas();
            } else {
                JOptionPane.showMessageDialog(this, "No se pudo eliminar el documento.");
            }
        });
       
        btnEliminarDoc.setBackground(new Color(220, 53, 69)); // rojo
        btnEliminarDoc.setForeground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 7;
        panelDocumentos.add(btnEliminarDoc, gbc);
    }
    
    private void crearPanelConvenios() {
        panelConvenios = new JPanel(new BorderLayout());
        panelConvenios.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel superior con título y buscador
        JPanel panelNorth = new JPanel(new BorderLayout());
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titulo = new JLabel("Listado de Convenios y Trámites");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        panelTitulo.add(titulo);

        JButton btnActualizar = new JButton("Actualizar");
        btnActualizar.addActionListener(e -> actualizarTablas());
        panelTitulo.add(btnActualizar);

        panelNorth.add(panelTitulo, BorderLayout.NORTH);
        
        JButton btnExportAll = new JButton("Exportar Todo (.txt)");
        btnExportAll.addActionListener(ev -> {
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Exportar datos a archivo TXT");
            fc.setSelectedFile(new File("export_all.txt"));
            int opt = fc.showSaveDialog(this);
            if (opt == JFileChooser.APPROVE_OPTION) {
                File f = fc.getSelectedFile();
                boolean ok = exportarTodoAUnTexto(f);
                if (ok) JOptionPane.showMessageDialog(this, "Exportación exitosa: " + f.getAbsolutePath(), "Exportar", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        panelTitulo.add(btnExportAll);

        // --- Panel buscador
        JPanel panelBuscar = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        panelBuscar.add(new JLabel("Buscar en:"), gbc);

        String[] opciones = {"Estudiantes por nombre", "Convenios por ID", "Trámites por ID/Estado"};
        JComboBox<String> comboModo = new JComboBox<>(opciones);
        gbc.gridx = 1; gbc.weightx = 0.4; gbc.fill = GridBagConstraints.HORIZONTAL;
        panelBuscar.add(comboModo, gbc);

        JTextField txtBuscar = new JTextField();
        txtBuscar.setToolTipText("Texto a buscar. Ej: 'Bruno', 'A-2025', 'T-1' o 'EN_PROCESO'");
        gbc.gridx = 2; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        panelBuscar.add(txtBuscar, gbc);

        JButton btnBuscar = new JButton("Buscar");
        gbc.gridx = 3; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        panelBuscar.add(btnBuscar, gbc);

        // Filtro de estado (solo para trámites)
        gbc.gridx = 0; gbc.gridy = 1;
        panelBuscar.add(new JLabel("Filtro estado (trámites):"), gbc);
        JComboBox<String> comboEstado = new JComboBox<>();
        comboEstado.addItem("Todos");
        for (Tramite.Estado s : Tramite.Estado.values()) comboEstado.addItem(s.name());
        gbc.gridx = 1; gbc.gridwidth = 1;
        panelBuscar.add(comboEstado, gbc);

        panelNorth.add(panelBuscar, BorderLayout.SOUTH);

        panelConvenios.add(panelNorth, BorderLayout.NORTH);

        // Split pane para mostrar convenios y trámites
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);

        // Tabla de convenios
        String[] columnasConvenios = {"ID", "Nombre", "Universidad", "País", "Duración", "Carrera"};
        modeloTablaConvenios = new DefaultTableModel(columnasConvenios, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tablaConvenios = new JTable(modeloTablaConvenios);
        JScrollPane scrollConvenios = new JScrollPane(tablaConvenios);
        scrollConvenios.setBorder(BorderFactory.createTitledBorder("Convenios"));

        // Tabla de trámites
        String[] columnasTramites = {"ID Trámite", "Convenio", "Estudiante", "Estado", "Documentos"};
        modeloTablaTramites = new DefaultTableModel(columnasTramites, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tablaTramites = new JTable(modeloTablaTramites);
        JScrollPane scrollTramites = new JScrollPane(tablaTramites);
        scrollTramites.setBorder(BorderFactory.createTitledBorder("Trámites"));

        splitPane.setTopComponent(scrollConvenios);
        splitPane.setBottomComponent(scrollTramites);
        panelConvenios.add(splitPane, BorderLayout.CENTER);

        // Panel inferior con botón ver detalles
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnDetalles = new JButton("Ver Detalles del Trámite");
        btnDetalles.addActionListener(e -> {
            int fila = tablaTramites.getSelectedRow();
            if (fila >= 0) {
                String idTramite = (String) modeloTablaTramites.getValueAt(fila, 0);
                String idConvenio = (String) modeloTablaTramites.getValueAt(fila, 1);
                mostrarDetallesTramite(idConvenio, idTramite);
            } else {
                JOptionPane.showMessageDialog(this, "Seleccione un trámite", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        panelBotones.add(btnDetalles);
        panelConvenios.add(panelBotones, BorderLayout.SOUTH);

        // --- Lógica de búsqueda: acciones y refresco de tablas
        Runnable limpiarTablas = () -> {
            modeloTablaConvenios.setRowCount(0);
            modeloTablaTramites.setRowCount(0);
        };

        // Helper para mostrar lista de convenios en la tabla
        Consumer<List<Convenio>> mostrarConveniosEnTabla = lista -> {
            modeloTablaConvenios.setRowCount(0);
            for (Convenio c : lista) {
                modeloTablaConvenios.addRow(new Object[]{
                    c.getIdConvenio(), c.getNombre(), c.getUniversidadSocia(),
                    c.getPais(), c.getDuracion(), c.getCarreraAsociada()
                });
            }
        };

        // Helper para mostrar lista de tramites en la tabla
        Consumer<List<Tramite>> mostrarTramitesEnTabla = lista -> {
            modeloTablaTramites.setRowCount(0);
            for (Tramite t : lista) {
                String docs = t.getDocumentos() == null || t.getDocumentos().isEmpty() ? "-" : t.getDocumentos().keySet().toString();
                String convenioId = "-";
                // intentar obtener convenio por búsqueda inversa: (si Tramite no tiene convenio directo)
                for (Convenio c : herramientas.getConvenios()) {
                    if (c.getTramites().contains(t)) { convenioId = c.getIdConvenio(); break; }
                }
                String estudiante = t.getEstudiante() == null ? "-" : t.getEstudiante().getRut();
                modeloTablaTramites.addRow(new Object[]{t.getIdTramite(), convenioId, estudiante, t.getEstado().name(), docs});
            }
        };

        // Acción del botón Buscar
        btnBuscar.addActionListener(e -> {
            String modo = (String) comboModo.getSelectedItem();
            String q = txtBuscar.getText().trim();
            // limpiar tablas
            modeloTablaConvenios.setRowCount(0);
            modeloTablaTramites.setRowCount(0);

            try {
                if ("Estudiantes por nombre".equals(modo)) {
                    if (q.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Ingrese un nombre o parte del nombre", "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    // Buscar estudiantes por nombre (usa tu método del Control)
                    List<Estudiante> encontrados = herramientas.buscarEstudiantesPorNombre(q);
                    if (encontrados == null || encontrados.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Sin resultados", "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    // REQUISITO: La tabla superior mostrará los convenios a los que el/los estudiantes han postulado.
                    // Construimos un conjunto de convenios relacionados y una lista de trámites del/los estudiantes.
                    LinkedHashSet<Convenio> conveniosRelacionados = new LinkedHashSet<>();
                    List<Tramite> tramitesEncontrados = new ArrayList<>();

                    for (Estudiante s : encontrados) {
                        for (Convenio c : herramientas.getConvenios()) {
                            for (Tramite t : c.getTramites()) {
                                if (t.getEstudiante() != null && t.getEstudiante().getRut().equals(s.getRut())) {
                                    conveniosRelacionados.add(c);
                                    tramitesEncontrados.add(t);
                                }
                            }
                        }
                    }

                    // Mostrar convenios relacionados en la tabla superior
                    for (Convenio c : conveniosRelacionados) {
                        modeloTablaConvenios.addRow(new Object[]{
                            c.getIdConvenio(), c.getNombre(), c.getUniversidadSocia(),
                            c.getPais(), c.getDuracion(), c.getCarreraAsociada()
                        });
                    }

                    // Mostrar trámites del/los estudiantes en la tabla inferior
                    for (Tramite t : tramitesEncontrados) {
                        String docs = (t.getDocumentos() == null || t.getDocumentos().isEmpty()) ? "-" : t.getDocumentos().keySet().toString();
                        String convenioId = "-";
                        // Obtener convenido padre (si Tramite no guarda referencia)
                        for (Convenio c : herramientas.getConvenios()) {
                            if (c.getTramites().contains(t)) { convenioId = c.getIdConvenio(); break; }
                        }
                        String estudianteRut = t.getEstudiante() == null ? "-" : t.getEstudiante().getRut();
                        modeloTablaTramites.addRow(new Object[]{t.getIdTramite(), convenioId, estudianteRut, t.getEstado().name(), docs});
                    }

                    // Si quieres, seleccionar la primera fila de convenios para enfoque visual
                    if (modeloTablaConvenios.getRowCount() > 0) {
                        // tablaConvenios.setRowSelectionInterval(0, 0); // requiere referencia a la JTable
                    }

                } else if ("Convenios por ID".equals(modo)) {
                    if (q.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Ingrese un ID o parte del ID", "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    List<Convenio> encontrados = herramientas.buscarConveniosPorId(q);
                    if (encontrados == null || encontrados.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Sin resultados", "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    // mostrar convenios y sus trámites (igual que antes)
                    for (Convenio c : encontrados) {
                        modeloTablaConvenios.addRow(new Object[]{
                            c.getIdConvenio(), c.getNombre(), c.getUniversidadSocia(),
                            c.getPais(), c.getDuracion(), c.getCarreraAsociada()
                        });
                    }
                    List<Tramite> tramites = new ArrayList<>();
                    for (Convenio c : encontrados) tramites.addAll(c.getTramites());
                    for (Tramite t : tramites) {
                        String docs = (t.getDocumentos() == null || t.getDocumentos().isEmpty()) ? "-" : t.getDocumentos().keySet().toString();
                        String estudianteRut = t.getEstudiante() == null ? "-" : t.getEstudiante().getRut();
                        modeloTablaTramites.addRow(new Object[]{t.getIdTramite(), /*convenio*/ t.getIdTramite(), estudianteRut, t.getEstado().name(), docs});
                    }

                } else { // "Trámites por ID/Estado"
                    if (q.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Ingrese texto para buscar trámites", "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    List<Tramite> encontrados = herramientas.buscarTramitesPorTexto(q);
                    if (encontrados == null || encontrados.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Sin resultados", "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    // Mostrar trámites en la tabla inferior y convenios relacionados en la superior
                    LinkedHashSet<Convenio> conveniosRelacionados = new LinkedHashSet<>();
                    for (Tramite t : encontrados) {
                        for (Convenio c : herramientas.getConvenios()) {
                            if (c.getTramites().contains(t)) conveniosRelacionados.add(c);
                        }
                    }
                    for (Convenio c : conveniosRelacionados) {
                        modeloTablaConvenios.addRow(new Object[]{
                            c.getIdConvenio(), c.getNombre(), c.getUniversidadSocia(),
                            c.getPais(), c.getDuracion(), c.getCarreraAsociada()
                        });
                    }
                    for (Tramite t : encontrados) {
                        String docs = (t.getDocumentos() == null || t.getDocumentos().isEmpty()) ? "-" : t.getDocumentos().keySet().toString();
                        String convenioId = "-";
                        for (Convenio c : herramientas.getConvenios()) {
                            if (c.getTramites().contains(t)) { convenioId = c.getIdConvenio(); break; }
                        }
                        String estudianteRut = t.getEstudiante() == null ? "-" : t.getEstudiante().getRut();
                        modeloTablaTramites.addRow(new Object[]{t.getIdTramite(), convenioId, estudianteRut, t.getEstado().name(), docs});
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error en búsqueda: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Doble clic en fila de tablaConvenios: mostrar detalle del convenio
        tablaConvenios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int fila = tablaConvenios.getSelectedRow();
                    if (fila >= 0) {
                        String id = (String) modeloTablaConvenios.getValueAt(fila, 0);
                        // si la fila corresponde a un convenio real, buscar y mostrar detalles
                        Convenio c = herramientas.buscarConvenio(id);
                        if (c != null) {
                            JOptionPane.showMessageDialog(panelConvenios,
                                c.toString(),
                                "Detalle Convenio", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            // quizá es una fila de estudiante (RUT) creada por búsqueda; en ese caso mostrar estudiante
                            Estudiante s = herramientas.buscarEstudiante(id);
                            if (s != null) {
                                JOptionPane.showMessageDialog(panelConvenios,
                                    s.toString(),
                                    "Detalle Estudiante", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }
                }
            }
        });

        // Doble clic en fila de tablaTramites: mostrar detalles del trámite
        tablaTramites.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int fila = tablaTramites.getSelectedRow();
                    if (fila >= 0) {
                        String idTramite = (String) modeloTablaTramites.getValueAt(fila, 0);
                        String idConvenio = (String) modeloTablaTramites.getValueAt(fila, 1);
                        mostrarDetallesTramite(idConvenio, idTramite);
                    }
                }
            }
        });
    }

    private void crearPanelRequisitos() {
        panelRequisitos = new JPanel(new GridBagLayout());
        panelRequisitos.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Título
        JLabel titulo = new JLabel("Configurar Requisitos de Convenio");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 3;
        panelRequisitos.add(titulo, gbc);
        
        // Convenio
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        panelRequisitos.add(new JLabel("Convenio:"), gbc);
        
        JComboBox<String> comboConvenioReq = new JComboBox<>();
        gbc.gridx = 1;
        gbc.gridwidth = 2;
        panelRequisitos.add(comboConvenioReq, gbc);
        
        // Lista de requisitos actuales
        DefaultListModel<TipoDocumento> modeloRequisitos = new DefaultListModel<>();
        JList<TipoDocumento> listaRequisitos = new JList<>(modeloRequisitos);
        JScrollPane scrollRequisitos = new JScrollPane(listaRequisitos);
        scrollRequisitos.setPreferredSize(new Dimension(250, 150));
        scrollRequisitos.setBorder(BorderFactory.createTitledBorder("Requisitos Actuales"));
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        panelRequisitos.add(scrollRequisitos, gbc);
        
        // Lista de tipos disponibles
        DefaultListModel<TipoDocumento> modeloDisponibles = new DefaultListModel<>();
        for (TipoDocumento tipo : TipoDocumento.values()) {
            modeloDisponibles.addElement(tipo);
        }
        JList<TipoDocumento> listaDisponibles = new JList<>(modeloDisponibles);
        JScrollPane scrollDisponibles = new JScrollPane(listaDisponibles);
        scrollDisponibles.setPreferredSize(new Dimension(250, 150));
        scrollDisponibles.setBorder(BorderFactory.createTitledBorder("Tipos Disponibles"));
        gbc.gridx = 2; gbc.gridy = 2;
        gbc.gridwidth = 1;
        panelRequisitos.add(scrollDisponibles, gbc);
        
        // Botones de acción
        JPanel panelBotones = new JPanel(new GridLayout(2, 1, 5, 5));
        JButton btnAgregar = new JButton("← Agregar");
        JButton btnQuitar = new JButton("Quitar →");
        panelBotones.add(btnAgregar);
        panelBotones.add(btnQuitar);
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        panelRequisitos.add(panelBotones, gbc);
        
        // Área de información
        JTextArea areaInfoReq = new JTextArea(5, 40);
        areaInfoReq.setEditable(false);
        areaInfoReq.setBorder(BorderFactory.createTitledBorder("Información"));
        JScrollPane scrollInfo = new JScrollPane(areaInfoReq);
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panelRequisitos.add(scrollInfo, gbc);
        
        // Actualizar combo
        actualizarComboConvenios(comboConvenioReq);
        
        // Listener para cambio de convenio
        comboConvenioReq.addActionListener(e -> {
            String idConvenio = (String) comboConvenioReq.getSelectedItem();
            if (idConvenio != null) {
                Convenio convenio = herramientas.buscarConvenio(idConvenio.split(" - ")[0]);
                if (convenio != null) {
                    modeloRequisitos.clear();
                    for (TipoDocumento req : convenio.getRequisitos()) {
                        modeloRequisitos.addElement(req);
                    }
                    areaInfoReq.setText("Convenio: " + convenio.getNombre() + "\n" +
                                       "Total de requisitos: " + convenio.getRequisitos().size() + "\n" +
                                       "Trámites activos: " + convenio.getTramites().size());
                }
            }
        });
        
        // Listener para cambio de pestaña
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedComponent() == panelRequisitos) {
                actualizarComboConvenios(comboConvenioReq);
            }
        });
        
        // Acción agregar requisito
        btnAgregar.addActionListener(e -> {
            String idConvenio = (String) comboConvenioReq.getSelectedItem();
            TipoDocumento tipoSeleccionado = listaDisponibles.getSelectedValue();
            
            if (idConvenio == null || tipoSeleccionado == null) {
                JOptionPane.showMessageDialog(this, 
                    "Seleccione un convenio y un tipo de documento", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Convenio convenio = herramientas.buscarConvenio(idConvenio.split(" - ")[0]);
            if (convenio != null) {
                if (!convenio.getRequisitos().contains(tipoSeleccionado)) {
                    convenio.agregarRequisito(tipoSeleccionado);
                    modeloRequisitos.addElement(tipoSeleccionado);
                    areaInfoReq.setText("Requisito agregado: " + tipoSeleccionado + "\n" +
                                       "Total de requisitos: " + convenio.getRequisitos().size() + "\n" +
                                       "Trámites revalidados automáticamente");
                    actualizarTablas();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Este requisito ya existe en el convenio", 
                        "Información", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        // Acción quitar requisito
        btnQuitar.addActionListener(e -> {
            String idConvenio = (String) comboConvenioReq.getSelectedItem();
            TipoDocumento tipoSeleccionado = listaRequisitos.getSelectedValue();
            
            if (idConvenio == null || tipoSeleccionado == null) {
                JOptionPane.showMessageDialog(this, 
                    "Seleccione un convenio y un requisito a quitar", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Convenio convenio = herramientas.buscarConvenio(idConvenio.split(" - ")[0]);
            if (convenio != null) {
                convenio.quitarRequisito(tipoSeleccionado);
                modeloRequisitos.removeElement(tipoSeleccionado);
                areaInfoReq.setText("Requisito eliminado: " + tipoSeleccionado + "\n" +
                                   "Total de requisitos: " + convenio.getRequisitos().size() + "\n" +
                                   "Trámites revalidados automáticamente");
                actualizarTablas();
            }
        });
    }
    
    private void crearPanelGestionConvenios() {
        panelGestionConvenios = new JPanel(new GridBagLayout());
        panelGestionConvenios.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.anchor = GridBagConstraints.WEST;

        // Título
        JLabel titulo = new JLabel("Gestión de Convenios");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelGestionConvenios.add(titulo, gbc);

        // Separador
        gbc.gridy = 1;
        JSeparator sep = new JSeparator();
        sep.setPreferredSize(new Dimension(700, 1));
        panelGestionConvenios.add(sep, gbc);

        // Etiquetas + campos (cada fila tiene su propio gbc.gridy)
        gbc.gridwidth = 1;

        // ID
        gbc.gridy = 2; gbc.gridx = 0;
        panelGestionConvenios.add(new JLabel("ID Convenio:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField txtId = new JTextField(20);
        panelGestionConvenios.add(txtId, gbc);

        // Nombre
        gbc.gridy = 3; gbc.gridx = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        panelGestionConvenios.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField txtNombre = new JTextField(20);
        panelGestionConvenios.add(txtNombre, gbc);

        // Universidad socia
        gbc.gridy = 4; gbc.gridx = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        panelGestionConvenios.add(new JLabel("Universidad Socia:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField txtUni = new JTextField(20);
        panelGestionConvenios.add(txtUni, gbc);

        // País
        gbc.gridy = 5; gbc.gridx = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        panelGestionConvenios.add(new JLabel("País:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField txtPais = new JTextField(20);
        panelGestionConvenios.add(txtPais, gbc);

        // Duración
        gbc.gridy = 6; gbc.gridx = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        panelGestionConvenios.add(new JLabel("Duración:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField txtDur = new JTextField(20);
        panelGestionConvenios.add(txtDur, gbc);

        // Carrera asociada
        gbc.gridy = 7; gbc.gridx = 0; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
        panelGestionConvenios.add(new JLabel("Carrera Asociada:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextField txtCarr = new JTextField(20);
        panelGestionConvenios.add(txtCarr, gbc);

        // Botones (ponerlos en un panel con FlowLayout)
        JPanel panelBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        JButton btnAgregar = new JButton("Agregar Convenio");
        JButton btnModificar = new JButton("Modificar Convenio");
        JButton btnEliminar = new JButton("Eliminar Convenio");
        JButton btnListar = new JButton("Listar Convenios");
        panelBtns.add(btnAgregar);
        panelBtns.add(btnModificar);
        panelBtns.add(btnEliminar);
        panelBtns.add(btnListar);

        gbc.gridy = 8; gbc.gridx = 0; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.NONE; gbc.weightx = 0;
        panelGestionConvenios.add(panelBtns, gbc);

        // Área de resultado (texto) y tabla (lista)
        JTextArea areaResultado = new JTextArea(6, 60);
        areaResultado.setEditable(false);
        areaResultado.setBorder(BorderFactory.createTitledBorder("Resultado / Convenios"));

        JScrollPane scrollRes = new JScrollPane(areaResultado);

        String[] cols = {"ID", "Nombre", "Universidad", "País", "Duración", "Carrera"};
        DefaultTableModel modeloConvenios = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable tablaConvenios = new JTable(modeloConvenios);
        JScrollPane scrollTabla = new JScrollPane(tablaConvenios);
        scrollTabla.setPreferredSize(new Dimension(750, 150));

        // Añadir areaResultado y tabla; dejar que ocupen el espacio restante
        gbc.gridy = 9; gbc.gridx = 0; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.BOTH; gbc.weightx = 1.0; gbc.weighty = 1.0;
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollRes, scrollTabla);
        split.setResizeWeight(0.3);
        panelGestionConvenios.add(split, gbc);

        // Restaurar constraints para llamadas posteriores
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.NONE;

        // Helper para refrescar lista y tabla
        Runnable refrescarLista = () -> {
            modeloConvenios.setRowCount(0);
            StringBuilder sb = new StringBuilder();
            for (Convenio c : herramientas.getConvenios()) {
                Object[] fila = {
                    c.getIdConvenio(), c.getNombre(), c.getUniversidadSocia(),
                    c.getPais(), c.getDuracion(), c.getCarreraAsociada()
                };
                modeloConvenios.addRow(fila);
                sb.append(c.getIdConvenio()).append(" - ").append(c.getNombre())
                  .append(" | ").append(c.getUniversidadSocia()).append(" | ")
                  .append(c.getPais()).append(" | ").append(c.getDuracion())
                  .append(" | ").append(c.getCarreraAsociada()).append("\n");
            }
            areaResultado.setText(sb.toString());
            // Si tienes otros combos que dependen de convenios, actualízalos aquí:
            try { actualizarCombos(); } catch (Exception ex) { /* noop si no existe */ }
        };

        // Acciones

        btnAgregar.addActionListener(e -> {
            String id = txtId.getText().trim();
            String nom = txtNombre.getText().trim();
            String uni = txtUni.getText().trim();
            String pais = txtPais.getText().trim();
            String dur = txtDur.getText().trim();
            String car = txtCarr.getText().trim();

            if (id.isEmpty() || nom.isEmpty() || uni.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID, Nombre y Universidad son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (herramientas.buscarConvenio(id) != null) {
                JOptionPane.showMessageDialog(this, "Ya existe un convenio con ese ID", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Set<TipoDocumento> requisitos = new HashSet<>(); // vacío por ahora
            Convenio c = new Convenio(id, nom, uni, pais, requisitos, dur, car);
            herramientas.agregarConvenio(c);
            JOptionPane.showMessageDialog(this, "Convenio agregado correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);

            // limpiar
            txtId.setText(""); txtNombre.setText(""); txtUni.setText("");
            txtPais.setText(""); txtDur.setText(""); txtCarr.setText("");
            refrescarLista.run();
        });

        btnModificar.addActionListener(e -> {
            String id = txtId.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese el ID del convenio a modificar en el campo ID Convenio", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Convenio c = herramientas.buscarConvenio(id);
            if (c == null) {
                JOptionPane.showMessageDialog(this, "No existe convenio con id: " + id, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String nom = txtNombre.getText().trim(); if (nom.isBlank()) nom = null;
            String uni = txtUni.getText().trim(); if (uni.isBlank()) uni = null;
            String pais = txtPais.getText().trim(); if (pais.isBlank()) pais = null;
            String dur = txtDur.getText().trim(); if (dur.isBlank()) dur = null;
            String car = txtCarr.getText().trim(); if (car.isBlank()) car = null;

            boolean ok = herramientas.editarConvenio(id, nom, uni, pais, dur, car);
            JOptionPane.showMessageDialog(this, ok ? "Convenio actualizado." : "No se pudo actualizar.", "Información", JOptionPane.INFORMATION_MESSAGE);
            refrescarLista.run();
        });

        btnEliminar.addActionListener(e -> {
            String id = txtId.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Ingrese el ID del convenio a eliminar en el campo ID Convenio", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Convenio c = herramientas.buscarConvenio(id);
            if (c == null) {
                JOptionPane.showMessageDialog(this, "No existe convenio con id: " + id, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int conf = JOptionPane.showConfirmDialog(this, "¿Confirmas eliminar el convenio " + id + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (conf != JOptionPane.YES_OPTION) return;

            boolean ok = herramientas.eliminarConvenio(id);
            JOptionPane.showMessageDialog(this, ok ? "Convenio eliminado." : "No se pudo eliminar.", "Información", JOptionPane.INFORMATION_MESSAGE);
            refrescarLista.run();
        });

        btnListar.addActionListener(e -> refrescarLista.run());

        // Doble click en tabla llena campos para editar/eliminar
        tablaConvenios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int fila = tablaConvenios.getSelectedRow();
                    if (fila >= 0) {
                        txtId.setText((String) modeloConvenios.getValueAt(fila, 0));
                        txtNombre.setText((String) modeloConvenios.getValueAt(fila, 1));
                        txtUni.setText((String) modeloConvenios.getValueAt(fila, 2));
                        txtPais.setText((String) modeloConvenios.getValueAt(fila, 3));
                        txtDur.setText((String) modeloConvenios.getValueAt(fila, 4));
                        txtCarr.setText((String) modeloConvenios.getValueAt(fila, 5));
                    }
                }
            }
        });

        // Inicializar contenido (en el EDT)
        SwingUtilities.invokeLater(refrescarLista);
    }
    
    // Métodos auxiliares
    
    private void actualizarCombos() {
        // Este método se llama cuando se necesita actualizar todos los combos
        // Por ejemplo, después de registrar un nuevo estudiante
    }
    
    private void actualizarComboConvenios(JComboBox<String> combo) {
        combo.removeAllItems();
        for (Convenio c : herramientas.getConvenios()) {
            combo.addItem(c.getIdConvenio() + " - " + c.getNombre());
        }
    }
    
    private void actualizarComboEstudiantes(JComboBox<String> combo) {
        combo.removeAllItems();
        for (Estudiante e : herramientas.getEstudiantes()) {
            combo.addItem(e.getRut());
        }
    }
    
    private void actualizarComboTramites(JComboBox<String> comboConvenio, JComboBox<String> comboTramite) {
        comboTramite.removeAllItems();
        String idConvenio = (String) comboConvenio.getSelectedItem();
        if (idConvenio != null) {
            Convenio convenio = herramientas.buscarConvenio(idConvenio.split(" - ")[0]);
            if (convenio != null) {
                for (Tramite t : convenio.getTramites()) {
                    comboTramite.addItem(t.getIdTramite());
                }
            }
        }
    }
    
    private void actualizarEstadoTramite(JComboBox<String> comboConvenio, JComboBox<String> comboTramite, JTextArea areaEstado) {
        String idConvenio = (String) comboConvenio.getSelectedItem();
        String idTramite = (String) comboTramite.getSelectedItem();
        
        if (idConvenio != null && idTramite != null) {
            Convenio convenio = herramientas.buscarConvenio(idConvenio.split(" - ")[0]);
            if (convenio != null) {
                Tramite tramite = convenio.getTramites().stream()
                    .filter(t -> t.getIdTramite().equals(idTramite))
                    .findFirst()
                    .orElse(null);
                
                if (tramite != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Estudiante: ").append(tramite.getEstudiante().getNombre()).append("\n");
                    sb.append("Estado: ").append(tramite.getEstado()).append("\n\n");
                    sb.append("Requisitos del convenio:\n");
                    for (TipoDocumento req : convenio.getRequisitos()) {
                        sb.append("  • ").append(req);
                        if (tramite.getDocumentos().containsKey(req)) {
                            sb.append(" ✓ (").append(tramite.getDocumentos().get(req).getNombreArchivo()).append(")");
                        } else {
                            sb.append(" ✗ (Pendiente)");
                        }
                        sb.append("\n");
                    }
                    sb.append("\nDocumentos subidos: ").append(tramite.getDocumentos().size());
                    sb.append("\nRequisitos totales: ").append(convenio.getRequisitos().size());
                    
                    areaEstado.setText(sb.toString());
                }
            }
        }
    }

    private void actualizarTablas() {
        // Actualizar tabla de convenios
        modeloTablaConvenios.setRowCount(0);
        for (Convenio c : herramientas.getConvenios()) {
            Object[] fila = {
                c.getIdConvenio(),
                c.getNombre(),
                c.getUniversidadSocia(),
                c.getPais(),
                c.getDuracion(),
                c.getCarreraAsociada()
            };
            modeloTablaConvenios.addRow(fila);
        }
        
        // Actualizar tabla de trámites
        modeloTablaTramites.setRowCount(0);
        for (Convenio c : herramientas.getConvenios()) {
            for (Tramite t : c.getTramites()) {
                Object[] fila = {
                    t.getIdTramite(),
                    c.getIdConvenio(),
                    t.getEstudiante().getNombre() + " (" + t.getEstudiante().getRut() + ")",
                    t.getEstado().toString(),
                    t.getDocumentos().size() + "/" + c.getRequisitos().size()
                };
                modeloTablaTramites.addRow(fila);
            }
        }
    }
    
    private void mostrarDetallesTramite(String idConvenio, String idTramite) {
        Convenio convenio = herramientas.buscarConvenio(idConvenio);
        if (convenio == null) return;
        
        Tramite tramite = convenio.getTramites().stream()
            .filter(t -> t.getIdTramite().equals(idTramite))
            .findFirst()
            .orElse(null);
        
        if (tramite == null) return;
        
        JDialog dialog = new JDialog(this, "Detalles del Trámite", true);
        dialog.setLayout(new BorderLayout());
        
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Información del trámite
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("<html><b>ID Trámite:</b></html>"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(tramite.getIdTramite()), gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("<html><b>Estudiante:</b></html>"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(tramite.getEstudiante().getNombre()), gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("<html><b>RUT:</b></html>"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(tramite.getEstudiante().getRut()), gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("<html><b>Convenio:</b></html>"), gbc);
        gbc.gridx = 1;
        panel.add(new JLabel(convenio.getNombre()), gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("<html><b>Estado:</b></html>"), gbc);
        gbc.gridx = 1;
        JLabel lblEstado = new JLabel(tramite.getEstado().toString());
        if (tramite.getEstado() == Tramite.Estado.COMPLETO) {
            lblEstado.setForeground(new Color(0, 128, 0));
        } else {
            lblEstado.setForeground(new Color(255, 140, 0));
        }
        panel.add(lblEstado, gbc);
        
        // Tabla de documentos
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        
        String[] columnas = {"Tipo Documento", "Estado", "Archivo", "Fecha"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);
        
        for (TipoDocumento req : convenio.getRequisitos()) {
            Object[] fila;
            if (tramite.getDocumentos().containsKey(req)) {
                DocumentoSubido doc = tramite.getDocumentos().get(req);
                fila = new Object[]{
                    req.toString(),
                    "✓ Subido",
                    doc.getNombreArchivo(),
                    doc.getFechaSubida().toString()
                };
            } else {
                fila = new Object[]{
                    req.toString(),
                    "✗ Pendiente",
                    "-",
                    "-"
                };
            }
            modelo.addRow(fila);
        }
        
        JTable tabla = new JTable(modelo);
        tabla.setEnabled(false);
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setPreferredSize(new Dimension(500, 150));
        panel.add(scroll, gbc);
        
        dialog.add(panel, BorderLayout.CENTER);
        
        // Botón cerrar
        JPanel panelBoton = new JPanel();
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dialog.dispose());
        panelBoton.add(btnCerrar);
        dialog.add(panelBoton, BorderLayout.SOUTH);
        
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    // Genera un único archivo TXT con 3 secciones: Convenios, Estudiantes, Trámites
    private boolean exportarTodoAUnTexto(File destino) {
        if (destino == null) return false;
        try (Writer w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destino), StandardCharsets.UTF_8))) {
            // Sección Convenios
            w.write("=== Convenios ===");
            w.write(System.lineSeparator());
            w.write("ID;Nombre;Universidad;País;Duración;Carrera;RequisitosCount;TramitesCount");
            w.write(System.lineSeparator());
            for (Convenio c : herramientas.getConvenios()) {
                String[] cols = new String[] {
                    safe(c.getIdConvenio()),
                    safe(c.getNombre()),
                    safe(c.getUniversidadSocia()),
                    safe(c.getPais()),
                    safe(c.getDuracion()),
                    safe(c.getCarreraAsociada()),
                    String.valueOf(c.getRequisitos() == null ? 0 : c.getRequisitos().size()),
                    String.valueOf(c.getTramites() == null ? 0 : c.getTramites().size())
                };
                w.write(String.join(";", escapeCols(cols)));
                w.write(System.lineSeparator());
            }
            w.write(System.lineSeparator());

            // Sección Estudiantes
            w.write("=== Estudiantes ===");
            w.write(System.lineSeparator());
            w.write("RUT;Nombre;Carrera;AnioIngreso;ConvenioAsociado");
            w.write(System.lineSeparator());
            for (Estudiante e : herramientas.getEstudiantes()) {
                String convenioNombre = e.getConvenio() == null ? "" : safe(e.getConvenio().getIdConvenio());
                String[] cols = new String[] {
                    safe(e.getRut()),
                    safe(e.getNombre()),
                    safe(e.getCarrera()),
                    String.valueOf(e.getAnioIngreso()),
                    convenioNombre
                };
                w.write(String.join(";", escapeCols(cols)));
                w.write(System.lineSeparator());
            }
            w.write(System.lineSeparator());

            // Sección Trámites (todos los trámites de todos los convenios)
            w.write("=== Trámites ===");
            w.write(System.lineSeparator());
            w.write("IDTramite;ConvenioID;EstudianteRUT;Estado;DocumentosCount;DocumentosDetalle");
            w.write(System.lineSeparator());
            for (Convenio c : herramientas.getConvenios()) {
                for (Tramite t : c.getTramites()) {
                    String docsCount = String.valueOf(t.getDocumentos() == null ? 0 : t.getDocumentos().size());
                    String docsDetalle = "-";
                    if (t.getDocumentos() != null && !t.getDocumentos().isEmpty()) {
                        List<String> parts = new ArrayList<>();
                        for (Map.Entry<TipoDocumento, DocumentoSubido> en : t.getDocumentos().entrySet()) {
                            String nombreArchivo = en.getValue() == null ? "" : en.getValue().getNombreArchivo();
                            parts.add(en.getKey().name() + ":" + safe(nombreArchivo));
                        }
                        docsDetalle = String.join(",", parts);
                    }
                    String[] cols = new String[] {
                        safe(t.getIdTramite()),
                        safe(c.getIdConvenio()),
                        t.getEstudiante() == null ? "" : safe(t.getEstudiante().getRut()),
                        t.getEstado() == null ? "" : t.getEstado().name(),
                        docsCount,
                        docsDetalle
                    };
                    w.write(String.join(";", escapeCols(cols)));
                    w.write(System.lineSeparator());
                }
            }

            w.flush();
            return true;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al exportar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // Helpers: sanea nulls y limita caracteres problemáticos
    private String safe(String s) {
        return s == null ? "" : s;
    }
    private String[] escapeCols(String[] cols) {
        String[] out = new String[cols.length];
        for (int i = 0; i < cols.length; i++) {
            out[i] = cols[i].replace(";", ","); // evita romper el separador
        }
        return out;
    }

    
    public static void main(String[] args) {
        try {
            // Configurar el Look and Feel del sistema
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            Main frame = new Main();
            frame.setVisible(true);
        });
    }
}
