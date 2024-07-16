import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;
//import java.util.concurrent.TimeUnit;

public class Blackjack {
   
    int boardWidth = 600;
    int boardHeight = 600; // 50px for text panel on top
    int cardWidth = 110; //ration 1/1.4
    int cardHeight = 154;
    int balance = 950;
    int betAmount = 50;
    boolean stand = false;
    boolean start = false;
    JFrame frame = new JFrame("Black Jack");
    JLabel textLabel = new JLabel();
    JPanel gamePanel = new JPanel(){
        @Override
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            if (start){
            if (stand){
               try{ 
                Image hiddenCardImg = new ImageIcon(getClass().getResource(hiddenCard.getImagePath())).getImage();
                g.drawImage(hiddenCardImg, 20, 20, cardWidth,cardHeight, null);
               }catch (Exception e ){
                e.printStackTrace();
               }
            }
            else { try{
                //draw hidden card
                Image hiddenCardImg = new ImageIcon(getClass().getResource("./cards/BACK.png")).getImage();
                g.drawImage(hiddenCardImg, 20, 20, cardWidth,cardHeight, null);
            } catch (Exception e){
                e.printStackTrace();
            }
           }   try{
                //draw dealer's hand
                for (int i = 0; i<dealerHand.size();i++){
                    Card card = dealerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImg, cardWidth +25 + (cardWidth +5)*i, 20,cardWidth, cardHeight,null);
                }
                //draw player's hand
                for (int i = 0;i<playerHand.size();i++){
                    Card card = playerHand.get(i);
                    Image cardImg = new ImageIcon(getClass().getResource(card.getImagePath())).getImage();
                    g.drawImage(cardImg, 20 + (cardWidth + 5)*i, 280, cardWidth, cardHeight, null);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
            if (stand){
               // resultLabel.setText(win());
                textLabel.setText("Player:"+ playerSum + " Dealer:" + dealerSum + " "+ win());
            }else{
            textLabel.setText("Player:"+ playerSum + " Dealer Showing: " + card.getValue() );
            }
        } else{
            textLabel.setText("Balance:"+ balance +" Bet Amount:" + betAmount);

        }

        }
    };
    JPanel buttonPanel = new JPanel();
    JButton hitButton = new JButton("Hit");
    JButton standButton = new JButton("Stand");
    JButton nextHandButton = new JButton("Next Hand");
    JButton startGameButton = new JButton("Start Game");
    JButton increaseBetButton = new JButton("Up Bet");
    JButton decreaseBetButton = new JButton("Down bet");
   // JPanel boardPanel = new JPanel();
   
   
   
    private class Card{
        String value;
        String type;

        Card(String v, String t){
            this.value = v;
            this.type = t;
        }
        public String toString(){
            return value + "-" + type;
        }
        public int getValue(){
            if ("AJQK".contains(value)){ //value is type of card AJQK
                if (value == "A"){
                    return 11;
                }
                return 10;
            }
            return Integer.parseInt(value);// no more letters at this point
        }
        public boolean isAce(){
        return value =="A";
        }
        public String getImagePath(){
            return "./cards/"+ toString() + ".png";
        }
    }
     
     
     ArrayList<Card> deck;
     Random random = new Random();// deck shuffel
     // dealer
     Card hiddenCard;
     Card card;
     ArrayList<Card> dealerHand;
     int dealerSum;
     int dealerAceCount;
    //player
    Card card1;
    Card card2;
    ArrayList<Card> playerHand;
    int playerSum;
    int playerAceCount;
        Blackjack(){
            frame.setVisible(true);
         frame.setSize(boardWidth, boardHeight);
         frame.setLocationRelativeTo(null);
         frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gamePanel.setLayout(new BorderLayout());
        gamePanel.setBackground(new Color(53,101,77));
        frame.add(gamePanel);
        gamePanel.add(textLabel);
       // gamePanel.add(resultLabel);
        
        //resultLabel.setVerticalAlignment();
        textLabel.setForeground(Color.white);
        textLabel.setFont(new Font("Arial",Font.BOLD, 20));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Balance:"+ balance +" Bet Amount:" + betAmount);
        hitButton.setFocusable(false);

       
        buttonPanel.add(startGameButton);
        startGameButton.setFocusable(false);
        buttonPanel.add(hitButton);
        hitButton.setEnabled(false);
        standButton.setFocusable(false);
        buttonPanel.add(standButton);
        standButton.setEnabled(false);
        buttonPanel.add(nextHandButton);
        buttonPanel.add(increaseBetButton);
        buttonPanel.add(decreaseBetButton);
        increaseBetButton.setFocusable(false);
        decreaseBetButton.setFocusable(false);
        nextHandButton.setFocusable(false);
        nextHandButton.setEnabled(false);
        
        frame.add(buttonPanel, BorderLayout.SOUTH);
        
        hitButton.addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent e){
            Card card = deck.remove(deck.size()-1);
            playerHand.add(card);
            playerAceCount += card.isAce() ? 1:0 ;
            playerSum += card.getValue();
          
           if (reducePlayerAce() >21){
            hitButton.setEnabled(false);
           }
            gamePanel.repaint();
            
          }  
        });

        standButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                hitButton.setEnabled(false);
                standButton.setEnabled(false);
                stand = true;
                gamePanel.repaint();
                while (dealerSum <17){
                    Card card = deck.remove(deck.size()-1);
                    dealerSum += card.getValue();
                    dealerAceCount += card.isAce()? 1:0;
                    dealerHand.add(card);
                   if(dealerSum >21 && dealerAceCount>0){
                    dealerSum -=10;
                   }
                    gamePanel.repaint();
                }
                if (win().contains("Lose")) balance-=betAmount;
                    
                if (win().contains("You Win")) balance+=betAmount;
                gamePanel.repaint();
            }
           
        });
        startGameButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                decreaseBetButton.setEnabled(start);
                increaseBetButton.setEnabled(start);
                start = true; 
                startGame();
                gamePanel.repaint();
                hitButton.setEnabled(true);
                standButton.setEnabled(true);
                startGameButton.setEnabled(false);
            }
        });
        increaseBetButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
              if (balance != 0){  
                balance -= 50;
                betAmount +=50;
                gamePanel.repaint();
              }
            }
        });
        decreaseBetButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(betAmount != 0){
                balance += 50;
                betAmount -=50;
                gamePanel.repaint();
                }
            }
        });
        nextHandButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                start = !start;
               nextHandButton.setEnabled(!start);
               decreaseBetButton.setEnabled(!start);
               increaseBetButton.setEnabled(!start);
               
               hitButton.setEnabled(start);
               standButton.setEnabled(start);
                if (deck.size()<7){
                    System.out.println("Shuffled deck");
                    buildDeck();
                    shuffleDeck();
                }
                stand=false;
                dealerHand = new ArrayList<Card>();
                dealerSum = 0;
                dealerAceCount =0;
                hiddenCard = deck.remove(deck.size()-1);
                dealerSum += hiddenCard.getValue();
                dealerAceCount += hiddenCard.isAce() ? 1 :0;// if has ace acecount++
                card = deck.remove(deck.size()-1);
                dealerSum += card.getValue();
                dealerAceCount += card.isAce() ? 1 : 0;
                dealerHand.add(card);
                System.out.println("Dealer:");
                System.out.println(hiddenCard + " "+ card);
                System.out.println(dealerHand);
                System.out.println(dealerSum);
                System.out.println(dealerAceCount);
                //player
                playerHand = new ArrayList<Card>();
                playerSum = 0;
                playerAceCount = 0;
                card1 = deck.remove(deck.size()-1);
                playerSum += card1.getValue();
                playerAceCount += card1.isAce() ? 1:0;
                card2 = deck.remove(deck.size()-1);
                playerSum += card2.getValue();
                playerAceCount += card2.isAce() ? 1:0;
                playerHand.add(card1);
                playerHand.add(card2);
                System.out.println("player");
                System.out.println(card1);
                System.out.println(card2);
                System.out.println(playerHand);
                System.out.println(playerSum);
                System.out.println(playerAceCount);
                gamePanel.repaint();
            }
        });
        gamePanel.repaint();

        startGame();
        
}
        public void startGame(){
        //deck
        buildDeck();
        shuffleDeck();
        // dealer
        dealerHand = new ArrayList<Card>();
        dealerSum = 0;
        dealerAceCount =0;
        hiddenCard = deck.remove(deck.size()-1);
        dealerSum += hiddenCard.getValue();
        dealerAceCount += hiddenCard.isAce() ? 1 :0;// if has ace acecount++
        card = deck.remove(deck.size()-1);
        dealerSum += card.getValue();
        dealerAceCount += card.isAce() ? 1 : 0;
        dealerHand.add(card);
        System.out.println("Dealer:");
        System.out.println(hiddenCard + " "+ card);
        System.out.println(dealerHand);
        System.out.println(dealerSum);
        System.out.println(dealerAceCount);
        //player
        playerHand = new ArrayList<Card>();
        playerSum = 0;
        playerAceCount = 0;
        card1 = deck.remove(deck.size()-1);
        playerSum += card1.getValue();
        playerAceCount += card1.isAce() ? 1:0;
        card2 = deck.remove(deck.size()-1);
        playerSum += card2.getValue();
        playerAceCount += card2.isAce() ? 1:0;
        playerHand.add(card1);
        playerHand.add(card2);
        System.out.println("player");
        System.out.println(card1);
        System.out.println(card2);
        System.out.println(playerHand);
        System.out.println(playerSum);
        System.out.println(playerAceCount);

}

public String win(){
    nextHandButton.setEnabled(true);

    if (playerSum >21){
    return "You Lose";
   }if(dealerSum >21){
    return "You Win";
   } if (dealerSum >playerSum ){
        return "You Lose";
    }if (dealerSum == playerSum){
        return "Push";
    }
    return "You Win";
}



public void buildDeck(){
    deck = new ArrayList<Card>();
    String[] values = {"A","2","3","4","5","6","7","8","9", "10", "J","Q","K"};
    String[] type = {"C","D","H","S"};

    for (int i =0; i<type.length; i++){
        for (int j = 0; j< values.length; j++){
            Card card = new Card(values[j],type[i]);
            deck.add(card);
        }
    }
System.out.println("build deck");
System.out.println(deck);
}
public void shuffleDeck(){
    for (int i = 0;i<deck.size(); i++){
       Card currCard = deck.get(i);
        int spot = random.nextInt(deck.size());
       Card randomCard = deck.get(spot);
       deck.set(i,randomCard);
       deck.set(spot,currCard);
    }
    System.out.println("after shuffle");
    System.out.println(deck);
}
public int reducePlayerAce(){
    while (playerSum>21 && playerAceCount >0){
        playerSum -=10;
        playerAceCount--;
    }
    return playerSum;
}
public int reduceDealerAce(){
    while (dealerSum>21 && dealerAceCount >0){
        dealerSum -=10;
        dealerAceCount--;
    }
    return dealerSum;
}
}
