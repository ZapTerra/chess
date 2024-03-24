package ui;
public class PrintChessboard {
    public static void printChessboard(boolean isWhite){
        System.out.print("\u001b[30m"); // Set text color to black
        System.out.print("\u001b[46m"); // Set background color to cyan
        System.out.println("\u001b[46m    h g f e  d c b a   ");
        for (int i = 0; i < 8; i++) {
            String s = "\u001b[46m " + (isWhite ? (i + 1) : (8 - i)) + " ";
            System.out.print(s);
            for (int j = 0; j < 8; j++) {
                int row = isWhite ? (7 - i) : i;
                int col = isWhite ? (7 - j) : j;

                if ((row + col) % 2 == 0) {
                    System.out.print("\u001b[47m"); // bg white
                } else {
                    System.out.print("\u001b[40m"); // bg black
                }
                if ((row == 1 || row == 6)) {
                    // Pawns
                    System.out.print((row == 1) ? "🏀" : "⚾");
                } else if ((row == 0 || row == 7) && (col == 0 || col == 7)) {
                    // Rooks
                    System.out.print((row == 0) ? "🗿" : "🛠️");
                } else if ((row == 0 || row == 7) && (col == 1 || col == 6)) {
                    // Knights
                    System.out.print((row == 0) ? "🦀" : "🐸");
                } else if ((row == 0 || row == 7) && (col == 2 || col == 5)) {
                    // Bishops
                    System.out.print((row == 0) ? "🪥" : "🦷");
                } else if ((row == 0 || row == 7) && col == 3) {
                    // Queens
                    System.out.print((row == 0) ? "🏳️‍🌈" : "💅");
                } else if (row == 0 || row == 7) {
                    // Kings
                    System.out.print((row == 0) ? "🐀" : "🐁");
                } else {
                    System.out.print(((row + col) % 2 == 0) ? "⬜" : "⬛");
                }
            }
            System.out.println(s);
        }
        System.out.println("\u001b[46m    h g f e  d c b a   ");
        System.out.print("\u001b[0m");
    }
}

