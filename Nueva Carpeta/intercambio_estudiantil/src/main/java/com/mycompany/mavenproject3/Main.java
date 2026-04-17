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
}
