package BKE.Helper;

import BKE.ApplicationState;
import BKE.Game.Player.IPlayer;
import BKE.Game.Player.NetworkPlayer;
import BKE.Game.Player.ZeeslagAIPlayer;
import BKE.Game.Variants.Zeeslag;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class Benchmarker {

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

                bw.write("PLAYERONE,PLAYERTWO,TURNS,WINNER,PLAYERONEHITS,PLAYERTWOHITS,PLAYERONEMISSES,PLAYERTWOMISSES");
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

                        bw.write(sb.toString());
                        bw.newLine();
                        if (statcount % 10 == 0){
                            bw.flush();
                        }
                        statcount ++;
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
                    z.initialize(new ZeeslagAIPlayer("AI1",z), new ZeeslagAIPlayer("AI2",z), false);
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

    public void stop(){
        _benchmarksRunning = false;
        for (int i = 0; i < _threads.size(); i++) {
            try {
                _threads.get(i).join(1000);
            } catch (InterruptedException e) {
                kill(_threads.get(i));
                throw new RuntimeException(e);
            }
        }
        try {
            _resultThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        _threads.clear();
    }

    public void kill(Thread t){
        t.interrupt();
    }
}
