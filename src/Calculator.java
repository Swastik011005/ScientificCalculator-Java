import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Stack;
import java.util.ArrayList;

public class Calculator extends JFrame implements ActionListener {
    private JTextField display;
    private StringBuilder expr;

    private final String[] buttons = {
            "C", "(", ")", "/",
            "7", "8", "9", "*",
            "4", "5", "6", "-",
            "1", "2", "3", "+",
            "0", ".", "=", "±",
            "sin", "cos", "tan", "sqrt", "log"
    };

    public Calculator() {
        setTitle("Scientific Calculator");
        setSize(400, 550);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10,10));
        getContentPane().setBackground(new Color(30,30,30));

        expr = new StringBuilder();

        // Display
        display = new JTextField();
        display.setEditable(false);
        display.setFont(new Font("Segoe UI", Font.BOLD, 28));
        display.setBackground(new Color(40,40,40));
        display.setForeground(Color.WHITE);
        display.setHorizontalAlignment(SwingConstants.RIGHT);
        display.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        add(display, BorderLayout.NORTH);

        // Buttons
        JPanel panel = new JPanel(new GridLayout(7,4,8,8));
        panel.setBackground(new Color(30,30,30));

        for(String text: buttons){
            JButton btn = new JButton(text);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 18));
            btn.setFocusPainted(false);
            btn.setBackground(new Color(60,63,65));
            btn.setForeground(Color.WHITE);
            btn.setBorder(BorderFactory.createLineBorder(new Color(50,50,50)));
            btn.addActionListener(this);
            panel.add(btn);
        }

        add(panel, BorderLayout.CENTER);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();

        switch (cmd) {
            case "C" -> {
                expr.setLength(0);
                display.setText("");
            }
            case "=" -> {
                try {
                    double result = evaluate(expr.toString());
                    display.setText(String.valueOf(result));
                    expr.setLength(0);
                } catch (Exception ex) {
                    display.setText("Error");
                    expr.setLength(0);
                }
            }
            case "±" -> {
                expr.insert(0, "(-");
                expr.append(")");
                display.setText(expr.toString());
            }
            case "sin","cos","tan","sqrt","log" -> {
                expr.append(cmd).append("(");
                display.setText(expr.toString());
            }
            default -> {
                expr.append(cmd);
                display.setText(expr.toString());
            }
        }
    }

    // ================= Evaluate Expression =================
    private double evaluate(String input){
        ArrayList<String> tokens = tokenize(input);
        ArrayList<String> rpn = toRPN(tokens);
        return evalRPN(rpn);
    }

    private ArrayList<String> tokenize(String input){
        ArrayList<String> tokens = new ArrayList<>();
        String number = "";
        for(int i=0;i<input.length();i++){
            char c = input.charAt(i);
            if(Character.isDigit(c) || c=='.'){
                number += c;
            } else {
                if(!number.isEmpty()){
                    tokens.add(number);
                    number="";
                }
                if(c=='+'||c=='-'||c=='*'||c=='/'||c=='('||c==')'){
                    tokens.add(""+c);
                } else { // functions
                    String func = "";
                    while(i<input.length() && Character.isLetter(input.charAt(i))){
                        func += input.charAt(i++);
                    }
                    i--;
                    tokens.add(func);
                }
            }
        }
        if(!number.isEmpty()) tokens.add(number);
        return tokens;
    }

    private int precedence(String op){
        return switch(op){
            case "+","-" -> 1;
            case "*","/" -> 2;
            default -> 0;
        };
    }

    private ArrayList<String> toRPN(ArrayList<String> tokens){
        ArrayList<String> output = new ArrayList<>();
        Stack<String> stack = new Stack<>();

        for(String token: tokens){
            if(token.matches("\\d+(\\.\\d+)?")){
                output.add(token);
            } else if(token.equals("sin") || token.equals("cos") || token.equals("tan")
                    || token.equals("sqrt") || token.equals("log")){
                stack.push(token);
            } else if(token.equals("(")){
                stack.push(token);
            } else if(token.equals(")")){
                while(!stack.isEmpty() && !stack.peek().equals("(")){
                    output.add(stack.pop());
                }
                if(!stack.isEmpty() && stack.peek().equals("(")) stack.pop();
                if(!stack.isEmpty() && (stack.peek().equals("sin") || stack.peek().equals("cos")
                        || stack.peek().equals("tan") || stack.peek().equals("sqrt") || stack.peek().equals("log"))){
                    output.add(stack.pop());
                }
            } else { // operators
                while(!stack.isEmpty() && precedence(token)<=precedence(stack.peek())){
                    output.add(stack.pop());
                }
                stack.push(token);
            }
        }
        while(!stack.isEmpty()) output.add(stack.pop());
        return output;
    }

    private double evalRPN(ArrayList<String> rpn){
        Stack<Double> stack = new Stack<>();
        for(String token: rpn){
            if(token.matches("\\d+(\\.\\d+)?")){
                stack.push(Double.parseDouble(token));
            } else if(token.equals("+")){
                double b=stack.pop(), a=stack.pop();
                stack.push(a+b);
            } else if(token.equals("-")){
                double b=stack.pop(), a=stack.pop();
                stack.push(a-b);
            } else if(token.equals("*")){
                double b=stack.pop(), a=stack.pop();
                stack.push(a*b);
            } else if(token.equals("/")){
                double b=stack.pop(), a=stack.pop();
                stack.push(a/b);
            } else if(token.equals("sin")){
                stack.push(Math.sin(Math.toRadians(stack.pop())));
            } else if(token.equals("cos")){
                stack.push(Math.cos(Math.toRadians(stack.pop())));
            } else if(token.equals("tan")){
                stack.push(Math.tan(Math.toRadians(stack.pop())));
            } else if(token.equals("sqrt")){
                stack.push(Math.sqrt(stack.pop()));
            } else if(token.equals("log")){
                stack.push(Math.log10(stack.pop()));
            }
        }
        return stack.pop();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Calculator::new);
    }
}
