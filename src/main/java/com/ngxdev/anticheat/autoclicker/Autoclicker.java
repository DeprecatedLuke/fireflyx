package com.ngxdev.anticheat.autoclicker;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Autoclicker {
    private List<ClickData> legit;
    private List<ClickData> cheat;

    public Autoclicker() {
        this.legit = new ArrayList<ClickData>();
        this.cheat = new ArrayList<ClickData>();
    }

    public List<ClickData> getLegit() {
        return this.legit;
    }

    public void setLegit(final List<ClickData> legit) {
        this.legit = legit;
    }

    public List<ClickData> getCheat() {
        return this.cheat;
    }

    public void setCheat(final List<ClickData> cheat) {
        this.cheat = cheat;
    }

    public boolean save(final ClickData data) {
        // store somewhere?
        //try {
        //    String path = this.getOwner().getDataFolder().getAbsolutePath() + File.separator + "clicks" + File.separator;
        //    if (data.isLegit()) {
        //        path = path.concat("legit" + File.separator);
        //    }
        //    else {
        //        path = path.concat("cheat" + File.separator);
        //    }
        //    final File dir = new File(path);
        //    if (!dir.exists()) {
        //        dir.mkdirs();
        //    }
        //    String name;
        //    if (data.getType() == null) {
        //        name = dir.listFiles().length + ".txt";
        //    }
        //    else {
        //        final File dir2 = new File(path + data.getType() + File.separator);
        //        if (!dir2.exists()) {
        //            dir2.mkdirs();
        //        }
        //        name = data.getType() + File.separator + dir.listFiles().length + ".txt";
        //    }
        //    File file = new File(path + name);
        //    if (!file.exists()) {
        //        file.createNewFile();
        //    }
        //    else {
        //        int i = dir.listFiles().length;
        //        while (file.exists()) {
        //            ++i;
        //            if (data.getType() != null) {
        //                file = new File(path + data.getType() + "/" + i + ".txt");
        //            }
        //            else {
        //                file = new File(path + i + ".txt");
        //            }
        //        }
        //    }
        //    String content = "";
        //    for (final ClicksPerSecond cps : data.getClicks()) {
        //        if (cps.getTicks().size() == 20 && cps.getClicks() > 0) {
        //            content = content.concat(cps.getClicks() + "\t");
        //            for (final int ticks : cps.getTicks()) {
        //                content = content.concat(ticks + "\t");
        //            }
        //            content = content.concat("\n");
        //        }
        //    }
        //    final FileWriter fw = new FileWriter(file.getAbsoluteFile());
        //    final BufferedWriter bw = new BufferedWriter(fw);
        //    bw.write(content);
        //    bw.close();
        //    if (data.isLegit()) {
        //        this.legit.add(data);
        //    }
        //    else {
        //        this.cheat.add(data);
        //    }
        //    return true;
        //}
        //catch (IOException e) {
        //    e.printStackTrace();
        //    return false;
        //}
        return true;
    }

    public void loadAll(final boolean legit) {
        // load for website?
        //String path = this.getOwner().getDataFolder().getAbsolutePath() + File.separator + "clicks" + File.separator;
        //if (legit) {
        //    path = path.concat("legit" + File.separator);
        //}
        //else {
        //    path = path.concat("cheat" + File.separator);
        //}
        //BufferedReader br = null;
        //try {
        //    final File dir = new File(path);
        //    if (dir.exists()) {
        //        for (final File file : dir.listFiles()) {
        //            if (file.isDirectory()) {
        //                for (final File actualFile : file.listFiles()) {
        //                    if (!actualFile.isDirectory()) {
        //                        final ClickData data = new ClickData();
        //                        data.setType(file.getName());
        //                        br = new BufferedReader(new FileReader(actualFile.getAbsoluteFile()));
        //                        String sCurrentLine;
        //                        while ((sCurrentLine = br.readLine()) != null) {
        //                            final String[] array = sCurrentLine.split("\t");
        //                            if (array.length > 0) {
        //                                final ClicksPerSecond cps = new ClicksPerSecond();
        //                                final int key = Integer.parseInt(array[0]);
        //                                for (int i = 1; i < array.length; ++i) {
        //                                    cps.getTicks().add(Integer.parseInt(array[i]));
        //                                }
        //                                data.addClick(cps);
        //                            }
        //                        }
        //                        if (legit) {
        //                            this.legit.add(data);
        //                        }
        //                        else {
        //                            data.setLegit(false);
        //                            this.cheat.add(data);
        //                        }
        //                    }
        //                }
        //            }
        //            else {
        //                final ClickData data2 = new ClickData();
        //                br = new BufferedReader(new FileReader(file.getAbsoluteFile()));
        //                String sCurrentLine2;
        //                while ((sCurrentLine2 = br.readLine()) != null) {
        //                    final String[] array2 = sCurrentLine2.split("\t");
        //                    if (array2.length > 0) {
        //                        final ClicksPerSecond cps2 = new ClicksPerSecond();
        //                        final int key2 = Integer.parseInt(array2[0]);
        //                        for (int j = 1; j < array2.length; ++j) {
        //                            cps2.getTicks().add(Integer.parseInt(array2[j]));
        //                        }
        //                        data2.addClick(cps2);
        //                    }
        //                }
        //                if (legit) {
        //                    this.legit.add(data2);
        //                }
        //                else {
        //                    data2.setLegit(false);
        //                    this.cheat.add(data2);
        //                }
        //            }
        //        }
        //    }
        //}
        //catch (IOException e) {
        //    e.printStackTrace();
        //}
        //catch (Exception e2) {
        //    e2.printStackTrace();
        //}
        //finally {
        //    try {
        //        if (br != null) {
        //            br.close();
        //        }
        //    }
        //    catch (IOException ex) {
        //        ex.printStackTrace();
        //    }
        //}
    }

    public void analyse(final Player player, final ClickData data) {
        List<ClicksPerSecond> allCps = new ArrayList<>(data.getClicks());
        int totalClickAmount = 0;
        int frequency = 0;
        int totalFrequency = 0;
        int localFrequency = 0;
        int lastCps = 0;
        int upDown = 0;
        int localUpDown = 0;
        int oneZero = 0;
        int totalOneZero = 0;
        int lastTick = -1;
        int noZero = 0;
        int totalNoZero = 0;
        for (final ClicksPerSecond cps : allCps) {
            final int clicks = cps.getClicks();
            if (clicks >= 9) {
                ++totalOneZero;
                int localOneZero = 0;
                int localNoZero = 0;
                for (final int tick : cps.getTicks()) {
                    if (tick > 0) {
                        ++localNoZero;
                    }
                    if (lastTick >= 0) {
                        if (lastTick == 0 && tick > 0) {
                            ++localOneZero;
                        } else if (lastTick > 0 && tick == 0) {
                            ++localOneZero;
                        }
                    }
                    lastTick = tick;
                }
                if (localOneZero >= 16) {
                    ++oneZero;
                }
                if (clicks >= 15) {
                    ++totalNoZero;
                    if (localNoZero >= 18) {
                        ++noZero;
                    }
                }
                ++totalClickAmount;
                if (clicks == lastCps) {
                    localUpDown = 0;
                    if (clicks >= 11) {
                        ++totalFrequency;
                        if (++localFrequency >= 3) {
                            ++frequency;
                            localFrequency = 0;
                        }
                    } else {
                        localFrequency = 0;
                    }
                } else {
                    if (clicks == lastCps - 1) {
                        ++localUpDown;
                    } else if (clicks == lastCps + 1) {
                        ++localUpDown;
                    } else {
                        localUpDown = 0;
                    }
                    if (localUpDown >= 4) {
                        ++upDown;
                    }
                    localFrequency = 0;
                }
            } else {
                localFrequency = 0;
            }
            lastCps = clicks;
        }
        double doublesPercent = frequency / totalFrequency;
        doublesPercent = Math.round(doublesPercent * 100.0) / 100.0 * 100.0;
        player.sendMessage(ChatColor.GOLD + "CPS Frequency: " + frequency + " of " + totalFrequency + ChatColor.BLUE + " -> " + ChatColor.AQUA + doublesPercent + "%");
        player.sendMessage(ChatColor.GOLD + "UpDown: " + upDown + " of " + totalClickAmount);
        player.sendMessage(ChatColor.GOLD + "OneZero: " + oneZero + " of " + totalOneZero);
        player.sendMessage(ChatColor.GOLD + "NoZero: " + noZero + " of " + totalNoZero);
    }

    public Analysis compare(final ClickData data) {
        final List<ClickData> legitList = new ArrayList<>();
        legitList.addAll(this.legit);
        final List<ClickData> cheatList = new ArrayList<>();
        cheatList.addAll(this.cheat);
        final Map<String, Integer> typeMatches = new HashMap<>();
        final Map<String, Integer> typeTotal = new HashMap<>();
        int legitMatches = 0;
        int cheatMatches = 0;
        int totalLegit = 0;
        int totalCheat = 0;
        for (final ClicksPerSecond cps : data.getClicks()) {
            final int clicks = cps.getClicks();
            if (clicks >= 3) {
                for (final ClickData legitData : legitList) {
                    for (final ClicksPerSecond legitCps : legitData.getClicks()) {
                        if (legitCps.getClicks() == clicks) {
                            int localMatch = 0;
                            int localMismatch = 0;
                            for (int i = 0; i < cps.getTicks().size() && i < legitCps.getTicks().size(); ++i) {
                                final int millis = cps.getTicks().get(i);
                                final int legitMillis = legitCps.getTicks().get(i);
                                if (millis == legitMillis) {
                                    ++localMatch;
                                } else {
                                    ++localMismatch;
                                }
                            }
                            ++totalLegit;
                            if (localMismatch > 5) {
                                continue;
                            }
                            ++legitMatches;
                        }
                    }
                }
                for (final ClickData cheatData : cheatList) {
                    for (final ClicksPerSecond cheatCps : cheatData.getClicks()) {
                        if (cheatCps.getClicks() == clicks) {
                            final String type = cheatData.getType();
                            int localMatch2 = 0;
                            int localMismatch2 = 0;
                            for (int j = 0; j < cps.getTicks().size() && j < cheatCps.getTicks().size(); ++j) {
                                final int millis2 = cps.getTicks().get(j);
                                final int cheatMillis = cheatCps.getTicks().get(j);
                                if (millis2 == cheatMillis) {
                                    ++localMatch2;
                                } else {
                                    ++localMismatch2;
                                }
                            }
                            ++totalCheat;
                            if (type != null) {
                                if (typeTotal.containsKey(type)) {
                                    typeTotal.put(type, typeTotal.get(type) + 1);
                                } else {
                                    typeTotal.put(type, 1);
                                }
                            }
                            if (localMismatch2 > 5) {
                                continue;
                            }
                            if (type != null) {
                                if (typeMatches.containsKey(type)) {
                                    typeMatches.put(type, typeMatches.get(type) + 1);
                                } else {
                                    typeMatches.put(type, 1);
                                }
                            }
                            ++cheatMatches;
                        }
                    }
                }
            }
        }
        final Analysis analysis = new Analysis();
        analysis.setCheatMatches(cheatMatches);
        analysis.setLegitMatches(legitMatches);
        analysis.setTotalCheat(totalCheat);
        analysis.setTotalLegit(totalLegit);
        analysis.setTypeMatches(typeMatches);
        analysis.setTypeTotal(typeTotal);
        return analysis;
    }
}
