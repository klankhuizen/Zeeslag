package BKE.Helper;

import BKE.ApplicationState;
import BKE.Game.AI.BootZinkerinatorAI;
import BKE.Game.Player.IPlayer;
import BKE.Game.Player.NetworkPlayer;
import BKE.Game.Player.ZeeslagAIPlayer;
import BKE.Game.Variants.Zeeslag;

import javax.swing.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class Benchmarker {
    private int results = 0;

    ArrayList<Thread> _threads;
    Thread _resultThread;

    long _gamesPlayed = 0;
    LinkedBlockingQueue<MatchStats> _resultQueue = new LinkedBlockingQueue<>();
    private boolean _benchmarksRunning = false;

    public Benchmarker() {
        _threads = new ArrayList<>();
        _resultThread = new Thread(() -> {
            try {
                FileWriter fw = new FileWriter("benchmark-" + System.currentTimeMillis() + ".csv", true);
                BufferedWriter bw = new BufferedWriter(fw);

                bw.write("PLAYERONE,PLAYERTWO,TURNS,WINNER,PLAYERONEHITS,PLAYERTWOHITS,PLAYERONEMISSES,PLAYERTWOMISSES,PLAYERONETIME, PLAYERTWOTIME");
                bw.newLine();
                bw.flush();
                long statcount = 0;
                while (_benchmarksRunning){
                    MatchStats stat = _resultQueue.poll();
                    if (stat != null){
                        StringBuilder sb = new StringBuilder();
                        sb.append(stat.playerOne).append(",");
                        sb.append(stat.playerTwo).append(",");
                        sb.append(stat.turns).append(",");
                        sb.append(stat.winner).append(",");
                        sb.append(stat.playerOneHits).append(",");
                        sb.append(stat.playerTwoHits).append(",");
                        sb.append(stat.playerOneMisses).append(",");
                        sb.append(stat.playerTwoMisses).append(",");
                        sb.append(stat.playerOneTotalTurnTime).append(",");
                        sb.append(stat.playerTwoTotalTurnTime);

                        bw.write(sb.toString());
                        bw.newLine();
                        if (statcount % 1000 == 0){
                            bw.flush();
                        }
                        statcount ++;
                    }

                    if (statcount >= 100000) {
                        System.out.println("DONE");
                        bw.flush();
                        bw.close();
                        fw.close();
                        _benchmarksRunning = false;
                        stop(true);
                        return;
                    }
                }
                bw.flush();
                bw.close();
                fw.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
    }

    public void start(){
        _benchmarksRunning = true;
        _resultThread.start();
        for (int i = 0; i < _threads.size(); i++) {
            Thread t = _threads.get(i);
            t.start();
        }
    }

    public void initialize(){
        int cpus = Runtime.getRuntime().availableProcessors() - 2;

        if (cpus < 3) {
            cpus = 2;
        }

        for (int i = 0; i < cpus; i++) {
            Thread t = new Thread(() -> {
                while (_benchmarksRunning) {
                    Zeeslag z = new Zeeslag();
                    z.initialize(new BootZinkerinatorAI("BOOTZINKERINATOR",z), new ZeeslagAIPlayer("RANDOM",z), false);
                    IPlayer playerStarting = Math.random() > 0.5 ? z.getPlayerOne() : z.getPlayerTwo();
                    z.start(playerStarting.getName());
                    while(z.GetState() == ApplicationState.RUNNING){
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    try {
                        _resultQueue.offer(z.getMatchStats());
                        z.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            _threads.add(t);
        }
    }

    public void stop(boolean force){
        _benchmarksRunning = false;
        for (int i = 0; i < _threads.size(); i++) {
            try {
                if (force){
                    _threads.get(i).interrupt();
                } else {
                    _threads.get(i).join(1000);
                }
            } catch (InterruptedException e) {
                kill(_threads.get(i));
                throw new RuntimeException(e);
            }
        }
        try {
            _resultThread.join(10000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        _threads.clear();
    }

    public void kill(Thread t){
        t.interrupt();
    }
}
