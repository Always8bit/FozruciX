package com.LilG.Com;

import com.google.common.collect.ImmutableSortedSet;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;

import static com.LilG.Com.FozruciX.formatFileSize;

/**
 * Created by ggonz on 11/4/2015.
 * A debug window to monitor certain variables
 */
class DebugWindow extends JFrame{
    // Define constants, variables, and labels
    private static final int WIDTH = 800;
    private static final int HEIGHT = 220;
    private final JTextField currentNickTF;
	private final JTextField lastMessageTF;
	private final JTextField currDMTF;
	private final JTextField myPlayerNameTF;
	private final JTextField myPlayerHPTF;
	private final JTextField myPlayerXPTF;
	private final JTextField myFamiliarTF;
	private final JTextField myFamiliarHPTF;
	private final JTextField myFamiliarXPTF;
    private final JTextField memoryUsageTF = new JTextField(10);
    private final JTextField messageTF;
	private PircBotX bot;
	private String[] channels = {"#null"};
	private String selectedChannel = "#null";
	private DefaultComboBoxModel<String> comboBox;
    private Runtime runtime = Runtime.getRuntime();
    private final Timer timer = new Timer(1000, (ActionListener) e -> memoryUsageTF.setText("Current memory usage: " + formatFileSize(runtime.totalMemory() - runtime.freeMemory()) + "/" + formatFileSize(runtime.totalMemory())));

    DebugWindow(PircBotX bot) {
        JLabel currentNickL, lastMessageL, currDML, myPlayerNameL, myPlayerHPL, myPlayerXPL, myFamiliarL, myFamiliarHPL, myFamiliarXPL, memoryUsageL;
        String network = bot.getServerInfo().getNetwork();
        if (network == null) {
            network = bot.getServerHostname();
            network = network.substring(network.indexOf(".") + 1, network.lastIndexOf("."));
        }
        setTitle(bot.getNick() + " @ " + network);
        this.bot = bot;
        channels = getChannels(bot.getUserBot().getChannels());


        currentNickL = new JLabel("Currently Registered User", SwingConstants.LEFT);
        lastMessageL = new JLabel("Last message", SwingConstants.LEFT);
        currDML = new JLabel("Current Dungeon master", SwingConstants.LEFT);
        myPlayerNameL = new JLabel("My username", SwingConstants.LEFT);
        myPlayerHPL = new JLabel("HP", SwingConstants.LEFT);
        myPlayerXPL = new JLabel("XP", SwingConstants.LEFT);
        myFamiliarL = new JLabel("Familiar", SwingConstants.LEFT);
        myFamiliarHPL = new JLabel("Familiar HP", SwingConstants.LEFT);
        myFamiliarXPL = new JLabel("Familiar XP", SwingConstants.LEFT);
        memoryUsageL = new JLabel("Memory Usage", SwingConstants.LEFT);

        currentNickTF = new JTextField(10);
        lastMessageTF = new JTextField(10);
        currDMTF = new JTextField(10);
        myPlayerNameTF = new JTextField(10);
        myPlayerHPTF = new JTextField(10);
        myPlayerXPTF = new JTextField(10);
        myFamiliarTF = new JTextField(10);
        myFamiliarHPTF = new JTextField(10);
        myFamiliarXPTF = new JTextField(10);
        messageTF = new JTextField(512);
        Action action = new AbstractAction(){
            @Override
            public void actionPerformed(ActionEvent e){
                sendMessage();
                for(int i = 0; i > channels.length; i++){
                    comboBox.addElement(channels[i]);
                }
            }
        };
        messageTF.addActionListener(action);

        //Create Grid layout for window
        Container pane = getContentPane();
        pane.setLayout(new GridLayout(11, 2));


        super.setAlwaysOnTop(true);

        pane.add(currentNickL);
        pane.add(currentNickTF);

        pane.add(lastMessageL);
        pane.add(lastMessageTF);

        pane.add(currDML);
        pane.add(currDMTF);

        pane.add(myPlayerNameL);
        pane.add(myPlayerNameTF);

        pane.add(myPlayerHPL);
        pane.add(myPlayerHPTF);

        pane.add(myPlayerXPL);
        pane.add(myPlayerXPTF);

        pane.add(myFamiliarL);
        pane.add(myFamiliarTF);

        pane.add(myFamiliarHPL);
        pane.add(myFamiliarHPTF);

        pane.add(myFamiliarXPL);
        pane.add(myFamiliarXPTF);

        pane.add(memoryUsageL);
        pane.add(memoryUsageTF);

        comboBox = new DefaultComboBoxModel<>(channels);
        comboBox.addListDataListener(new ListDataListener(){
            @Override
            public void intervalAdded(ListDataEvent e){
                selectedChannel = (String) comboBox.getSelectedItem();
            }

            @Override
            public void intervalRemoved(ListDataEvent e){
                selectedChannel = (String) comboBox.getSelectedItem();
            }

            @Override
            public void contentsChanged(ListDataEvent e){
                selectedChannel = (String) comboBox.getSelectedItem();
            }
        });
        pane.add(new JComboBox<>(comboBox));
        pane.add(messageTF);

        setSize(WIDTH, HEIGHT);

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setVisible(true);

        selectedChannel = (String) comboBox.getSelectedItem();

        timer.start();
    }

    private String[] getChannels(ImmutableSortedSet<Channel> channel){
        Iterator<Channel> channels = channel.iterator();
        ArrayList<String> channelList = new ArrayList<>();
        while (channels.hasNext()){
            channelList.add(channels.next().getName());
        }
        return channelList.toArray(new String[channelList.size()]);
    }

	private void sendMessage(){
		selectedChannel = (String) comboBox.getSelectedItem();
        bot.send().message(selectedChannel, FozruciX.getScramble(messageTF.getText()));
        messageTF.setText("");
    }

    public void updateBot(PircBotX bot){
        this.bot = bot;
        channels = getChannels(bot.getUserBot().getChannels());
        selectedChannel = (String) comboBox.getSelectedItem();
        updateChannels();
        comboBox.setSelectedItem(selectedChannel);
    }

	private void updateChannels(){
		comboBox.removeAllElements();
		for(String channel : channels){
			comboBox.addElement(channel);
		}
	}

    public void setCurrentNick(String nick) {
        currentNickTF.setText(nick);
    }

    public void setMessage(String message) {
        lastMessageTF.setText(message);
    }

    public void setCurrDM(String DM) {
        currDMTF.setText(DM);
    }

    public void setPlayerName(String DNDName) {
        myPlayerNameTF.setText(DNDName);
    }

    public void setPlayerHP(String HP) {
        myPlayerHPTF.setText(HP);
    }

    public void setPlayerXP(String XP) {
        myPlayerXPTF.setText(XP);
    }

    public void setFamiliar(String Familiar) {
        myFamiliarTF.setText(Familiar);
    }

    public void setFamiliarHP(String HP) {
        myFamiliarHPTF.setText(HP);
    }

    public void setFamiliarXP(String XP) {
        myFamiliarXPTF.setText(XP);
    }

    public void setNick(String botNick) {
        setTitle(botNick);
    }

}

class DrawWindow extends Component {
	private final int map_size;
	private final int[][] map;
	private final Point currentPoint;

    public DrawWindow(int[][] map, int map_size, Point currentPoint) {
        this.map_size = map_size;
        this.map = map;
        this.currentPoint = currentPoint;
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        int dx = 0;
        int dy = 0;
        int scale = 5;
        g2d.scale(scale, scale);
        try {
            while (dy < map_size) {
                while (dx < map_size) {
                    if (dx == currentPoint.x && dy == currentPoint.y) {
                        g2d.setColor(Color.RED);
                    } else if (map[dx][dy] == 0) {
                        g2d.setColor(Color.CYAN);
                    } else if (map[dx][dy] == 1) {
                        g2d.setColor(Color.BLUE);
                    } else {
                        g2d.setColor(Color.MAGENTA);
                    }
                    g2d.drawLine(dx, dy, dx, dy);
                    dx++;
                    Thread.sleep(5);                 //1000 milliseconds is one second.
                }
                dy++;
                dx = 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
