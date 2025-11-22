package com.unimag.gui;

import com.unimag.eval.Evaluator;
import com.unimag.lexer.Lexer;
import com.unimag.lexer.Token;
import com.unimag.parser.Parser;
import com.unimag.parser.astNodes.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class ParserGUI extends JFrame {
    
    private JTextField inputField;
    private JTextArea outputArea;
    private JButton parseButton;
    private JButton clearButton;
    private JButton exitButton;
    private ASTPanel astPanel;
    
    public ParserGUI() {
        setTitle("Parser de Expresiones Trigonométricas");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        layoutComponents();
        
        setVisible(true);
    }
    
    private void initComponents() {
        inputField = new JTextField(30);
        inputField.setFont(new Font("Monospaced", Font.PLAIN, 16));
        inputField.addActionListener(e -> parseExpression());
        
        outputArea = new JTextArea(10, 40);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setEditable(false);
        outputArea.setBackground(new Color(40, 40, 40));
        outputArea.setForeground(Color.GREEN);
        
        astPanel = new ASTPanel();
        astPanel.setBackground(Color.WHITE);
        astPanel.setPreferredSize(new Dimension(800, 500));
        
        parseButton = new JButton("Parsear y Evaluar");
        parseButton.setFont(new Font("Arial", Font.BOLD, 14));
        parseButton.addActionListener(e -> parseExpression());
        
        clearButton = new JButton("Limpiar");
        clearButton.setFont(new Font("Arial", Font.PLAIN, 14));
        clearButton.addActionListener(e -> clearAll());
        
        exitButton = new JButton("Salir");
        exitButton.setFont(new Font("Arial", Font.PLAIN, 14));
        exitButton.addActionListener(e -> System.exit(0));
    }
    
    private void layoutComponents() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.add(new JLabel("Expresión: "));
        inputPanel.add(inputField);
        inputPanel.add(parseButton);
        inputPanel.add(clearButton);
        inputPanel.add(exitButton);
        
        topPanel.add(inputPanel, BorderLayout.NORTH);
        
        JLabel infoLabel = new JLabel("<html><b>Operadores:</b> +, -, *, /, ^  |  <b>Funciones:</b> sin, cos, tan  |  <b>Constantes:</b> pi, e  |  <b>Variables:</b> x, y, z, etc.</html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        topPanel.add(infoLabel, BorderLayout.SOUTH);
        
        add(topPanel, BorderLayout.NORTH);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(700);
        
        JPanel astContainer = new JPanel(new BorderLayout());
        astContainer.setBorder(BorderFactory.createTitledBorder("Árbol de Sintaxis Abstracta (AST)"));
        JScrollPane astScrollPane = new JScrollPane(astPanel);
        astContainer.add(astScrollPane, BorderLayout.CENTER);
        
        JPanel outputContainer = new JPanel(new BorderLayout());
        outputContainer.setBorder(BorderFactory.createTitledBorder("Consola de Salida"));
        JScrollPane outputScrollPane = new JScrollPane(outputArea);
        outputContainer.add(outputScrollPane, BorderLayout.CENTER);
        
        splitPane.setLeftComponent(astContainer);
        splitPane.setRightComponent(outputContainer);
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    private void parseExpression() {
        String input = inputField.getText().trim();
        
        if (input.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese una expresión", "Entrada vacía", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        outputArea.setText("");
        astPanel.setAST(null);
        
        try {
            appendOutput("=== FASE 1: TOKENIZACIÓN ===\n");
            Lexer lexer = new Lexer(input);
            List<Token> tokens = lexer.tokenize();
            int size = tokens.size() - 1;
            appendOutput("Tokens generados: " + size + "\n");
            for (Token token : tokens) {
                if (token.type().name().equals("EOF")) continue;
                appendOutput("  " + token + "\n");
            }
            
            appendOutput("\n=== FASE 2: ANÁLISIS SINTÁCTICO ===\n");
            Parser parser = new Parser(tokens);
            Node ast = parser.parse();
            appendOutput("✓ AST construido correctamente\n");
            
            astPanel.setAST(ast);
            
            appendOutput("\n=== FASE 3: EVALUACIÓN ===\n");
            Evaluator evaluator = new Evaluator(ast);
            
            Set<String> variables = evaluator.collectVariables();
            
            if (!variables.isEmpty()) {
                appendOutput("Variables detectadas: " + String.join(", ", variables) + "\n\n");
                
                Map<String, Double> varValues = new HashMap<>();
                for (String var : variables) {
                    String valueStr = JOptionPane.showInputDialog(this, "Ingrese valor para '" + var + "':");
                    
                    if (valueStr == null) {
                        appendOutput("Evaluación cancelada\n");
                        return;
                    }
                    
                    try {
                        double value = Double.parseDouble(valueStr.trim());
                        varValues.put(var, value);
                    } catch (NumberFormatException e) {
                        JOptionPane.showMessageDialog(this, "Valor inválido para variable '" + var + "'", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                
                evaluator.setVariables(varValues);
            }
            
            double result = evaluator.evaluate();
            
            appendOutput("\n╔══════════════════════════════════════╗\n");
            appendOutput(String.format("║  RESULTADO: %-20.10f ║\n", result));
            appendOutput("╚══════════════════════════════════════╝\n");
            
        } catch (Exception ex) {
            appendOutput("\n❌ ERROR: " + ex.getMessage() + "\n");
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearAll() {
        inputField.setText("");
        outputArea.setText("");
        astPanel.setAST(null);
        inputField.requestFocus();
    }
    
    private void appendOutput(String text) {
        outputArea.append(text);
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new ParserGUI();
        });
    }
}


class ASTPanel extends JPanel {
    private Node ast;
    private Map<Node, Point> nodePositions = new HashMap<>();
    private static final int NODE_RADIUS = 30;
    private static final int LEVEL_HEIGHT = 100;
    private static final int MIN_HORIZONTAL_SPACING = 80;
    
    public void setAST(Node ast) {
        this.ast = ast;
        this.nodePositions.clear();
        if (ast != null) calculatePositions();
        repaint();
    }
    
    private void calculatePositions() {
        if (ast == null) return;
        int totalWidth = calculateWidth(ast) * MIN_HORIZONTAL_SPACING;
        int startX = Math.max(totalWidth / 2, 400);
        positionNode(ast, startX, 50, totalWidth / 2);
    }
    
    private int calculateWidth(Node node) {
        if (node == null) return 0;
        if (node instanceof NumberNode || node instanceof VarNode) return 1;
        if (node instanceof UnaryNode u) return calculateWidth(u.getExpression());
        if (node instanceof FunctionNode f) return calculateWidth(f.getArgument());
        if (node instanceof BinaryNode b) return calculateWidth(b.getLeft()) + calculateWidth(b.getRight());
        return 1;
    }
    
    private void positionNode(Node node, int x, int y, int horizontalSpace) {
        if (node == null) return;
        nodePositions.put(node, new Point(x, y));
        
        if (node instanceof BinaryNode b) {
            int leftWidth = calculateWidth(b.getLeft());
            int rightWidth = calculateWidth(b.getRight());
            int totalWidth = leftWidth + rightWidth;
            int leftSpace = (int) (horizontalSpace * ((double) leftWidth / totalWidth));
            int rightSpace = (int) (horizontalSpace * ((double) rightWidth / totalWidth));
            positionNode(b.getLeft(), x - leftSpace / 2, y + LEVEL_HEIGHT, leftSpace);
            positionNode(b.getRight(), x + rightSpace / 2, y + LEVEL_HEIGHT, rightSpace);
        } else if (node instanceof UnaryNode u) {
            positionNode(u.getExpression(), x, y + LEVEL_HEIGHT, horizontalSpace);
        } else if (node instanceof FunctionNode f) {
            positionNode(f.getArgument(), x, y + LEVEL_HEIGHT, horizontalSpace);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (ast == null) {
            g.setColor(Color.GRAY);
            g.setFont(new Font("Arial", Font.ITALIC, 16));
            String message = "Ingrese una expresión para visualizar el AST";
            FontMetrics fm = g.getFontMetrics();
            g.drawString(message, (getWidth() - fm.stringWidth(message)) / 2, getHeight() / 2);
            return;
        }
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawEdges(g2d, ast);
        drawNodes(g2d, ast);
    }
    
    private void drawEdges(Graphics2D g2d, Node node) {
        if (node == null || !nodePositions.containsKey(node)) return;
        Point parentPos = nodePositions.get(node);
        g2d.setColor(new Color(100, 100, 100));
        g2d.setStroke(new BasicStroke(2));
        
        if (node instanceof BinaryNode b) {
            drawEdgeToChild(g2d, parentPos, b.getLeft());
            drawEdgeToChild(g2d, parentPos, b.getRight());
        } else if (node instanceof UnaryNode u) {
            drawEdgeToChild(g2d, parentPos, u.getExpression());
        } else if (node instanceof FunctionNode f) {
            drawEdgeToChild(g2d, parentPos, f.getArgument());
        }
    }
    
    private void drawEdgeToChild(Graphics2D g2d, Point parentPos, Node child) {
        if (child != null && nodePositions.containsKey(child)) {
            Point childPos = nodePositions.get(child);
            g2d.drawLine(parentPos.x, parentPos.y + NODE_RADIUS, childPos.x, childPos.y - NODE_RADIUS);
            drawEdges(g2d, child);
        }
    }
    
    private void drawNodes(Graphics2D g2d, Node node) {
        if (node == null || !nodePositions.containsKey(node)) return;
        Point pos = nodePositions.get(node);
        
        Color nodeColor;
        String label;
        
        if (node instanceof NumberNode n) {
            nodeColor = new Color(100, 149, 237); // Azul
            label = formatNumber(n.getValue());
        } else if (node instanceof VarNode v) {
            nodeColor = new Color(60, 179, 113); // Verde
            label = v.getIdentifier();
        } else if (node instanceof BinaryNode b) {
            nodeColor = new Color(255, 140, 0); // Naranja
            label = String.valueOf(b.getOperator());
        } else if (node instanceof UnaryNode u) {
            nodeColor = new Color(220, 20, 60); // Rojo
            label = String.valueOf(u.getOperator());
        } else if (node instanceof FunctionNode f) {
            nodeColor = new Color(147, 112, 219); // Púrpura
            label = f.getName();
        } else {
            nodeColor = Color.GRAY;
            label = "?";
        }
        
        g2d.setColor(nodeColor);
        g2d.fillOval(pos.x - NODE_RADIUS, pos.y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawOval(pos.x - NODE_RADIUS, pos.y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g2d.getFontMetrics();
        g2d.drawString(label, pos.x - fm.stringWidth(label) / 2, pos.y + fm.getHeight() / 4);
        
        if (node instanceof BinaryNode b) {
            drawNodes(g2d, b.getLeft());
            drawNodes(g2d, b.getRight());
        } else if (node instanceof UnaryNode u) {
            drawNodes(g2d, u.getExpression());
        } else if (node instanceof FunctionNode f) {
            drawNodes(g2d, f.getArgument());
        }
    }
    
    private String formatNumber(double value) {
        if (value == Math.floor(value)) return String.format("%.0f", value);
        if (Math.abs(value - Math.PI) < 0.000001) return "π";
        if (Math.abs(value - Math.E) < 0.000001) return "e";
        return String.valueOf(value);
    }
}
