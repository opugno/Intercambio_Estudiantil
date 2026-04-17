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

    
}
