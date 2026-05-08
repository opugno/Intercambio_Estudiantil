/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mavenproject3;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.function.Consumer;

public class PanelConvenios extends JPanel {

    private Control herramientas;
    private JTabbedPane tabbedPane;

    public PanelConvenios(Control herramientas, JTabbedPane tabbedPane) {

        this.herramientas = herramientas;
        this.tabbedPane = tabbedPane;

        initComponents();
    }
    private DefaultTableModel modeloTablaConvenios;
    private DefaultTableModel modeloTablaTramites;

    private void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel superior con título y buscador
        JPanel panelNorth = new JPanel(new BorderLayout());
        JPanel panelTitulo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titulo = new JLabel("Listado de Convenios y Trámites");
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        panelTitulo.add(titulo);

        JButton btnActualizar = new JButton("Actualizar");
        //btnActualizar.addActionListener(e -> actualizarTablas());
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
                //boolean ok = exportarTodoAUnTexto(f);
                //if (ok) JOptionPane.showMessageDialog(this, "Exportación exitosa: " + f.getAbsolutePath(), "Exportar", JOptionPane.INFORMATION_MESSAGE);
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

        add(panelNorth, BorderLayout.NORTH);

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
        add(splitPane, BorderLayout.CENTER);

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
        add(panelBotones, BorderLayout.SOUTH);

        // --- Lógica de búsqueda: acciones y refresco de tablas
        Runnable limpiarTablas = () -> {
            modeloTablaConvenios.setRowCount(0);
            modeloTablaTramites.setRowCount(0);
        };

        // Helper para mostrar lista de convenios en la tabla
        Consumer<java.util.List<Convenio>> mostrarConveniosEnTabla = lista -> {
            modeloTablaConvenios.setRowCount(0);
            for (Convenio c : lista) {
                modeloTablaConvenios.addRow(new Object[]{
                    c.getIdConvenio(), c.getNombre(), c.getUniversidadSocia(),
                    c.getPais(), c.getDuracion(), c.getCarreraAsociada()
                });
            }
        };

        // Helper para mostrar lista de tramites en la tabla
        Consumer<java.util.List<Tramite>> mostrarTramitesEnTabla = lista -> {
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
                    java.util.List<Estudiante> encontrados = herramientas.buscarEstudiantesPorNombre(q);
                    if (encontrados == null || encontrados.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Sin resultados", "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }

                    // REQUISITO: La tabla superior mostrará los convenios a los que el/los estudiantes han postulado.
                    // Construimos un conjunto de convenios relacionados y una lista de trámites del/los estudiantes.
                    LinkedHashSet<Convenio> conveniosRelacionados = new LinkedHashSet<>();
                    java.util.List<Tramite> tramitesEncontrados = new ArrayList<>();

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
                    java.util.List<Convenio> encontrados = herramientas.buscarConveniosPorId(q);
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
                    java.util.List<Tramite> tramites = new ArrayList<>();
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
                    java.util.List<Tramite> encontrados = herramientas.buscarTramitesPorTexto(q);
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
                            JOptionPane.showMessageDialog(
                                    PanelConvenios.this,
                                    c.toString(),
                                    "Detalle Convenio",
                                    JOptionPane.INFORMATION_MESSAGE
                                );
                        } else {
                            // quizá es una fila de estudiante (RUT) creada por búsqueda; en ese caso mostrar estudiante
                            Estudiante s = herramientas.buscarEstudiante(id);
                            if (s != null) {
                                JOptionPane.showMessageDialog(
                                    PanelConvenios.this,
                                    s.toString(),
                                    "Detalle Estudiante",
                                    JOptionPane.INFORMATION_MESSAGE
                                );
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
    private void mostrarDetallesTramite(String idConvenio, String idTramite) {
        Convenio convenio = herramientas.buscarConvenio(idConvenio);
        if (convenio == null) return;
        
        Tramite tramite = convenio.getTramites().stream()
            .filter(t -> t.getIdTramite().equals(idTramite))
            .findFirst()
            .orElse(null);
        
        if (tramite == null) return;
        
        JDialog dialog = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "Detalles del Trámite",
            true
            );
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
     public void actualizarTablas() {
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
}