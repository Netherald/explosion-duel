package com.github.highright1234.duel;
// 나 시작전이다 이새꺄
// 내가 이걸 까먹겠냐 ㅋㅋ
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.projecttl.plugin.duel.DuelPlugin;
import org.projecttl.plugin.duel.DuelPluginKt;
import xyz.namutree0345.langlib.structures.LanguageStructure;

import static com.github.highright1234.duel.DuelSend.*;

public class Commands implements CommandExecutor {
    LanguageStructure ls = DuelPluginKt.getSelectedLanguage();
    private void send_help(CommandSender sender) {
        sender.sendMessage("=========================");
        sender.sendMessage(ls.getNode("duel_help_duel_invite", ls.getNode("duel_invite_command")));
        sender.sendMessage(ls.getNode("duel_help_duel_accept", ls.getNode("duel_accept_command")));
        sender.sendMessage(ls.getNode("duel_help_duel_deny", ls.getNode("duel_deny_command")));
        sender.sendMessage("=========================");
    }

    private final DuelPlugin plugin;

    public Commands(DuelPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("duel")) {
            if (sender instanceof Player) {

                if (args.length == 1) {
                    if (!Before.playing) {
                        switch (args[0]) {
                            case "help":
                                send_help(sender);
                                return true;
                            case "invite":
                                sender.sendMessage("/duel invite <플레이어이름>");
                                return true;
                            case "accept":
                                sender.sendMessage("/duel accept <플레이어이름>");
                                return true;
                            case "deny":
                                sender.sendMessage("/duel deny <플레이어이름>");
                                return true;
                        } // 스위치 끝
                    } else {
                        sender.sendMessage("게임이 이미 작동중입니다.");
                    }
                } /*길이가 1일시*/ else if (args.length >= 2) {
                    Player player = Bukkit.getPlayerExact(args[1]);
                    if (player != null && (args[0].equals("accept") || args[0].equals("deny") || args[0].equals("invite"))) {
                        if (Before.check_playing()) {
                            sender.sendMessage("굳");
                            switch (args[0]) {
                                case "accept":
                                    duel_accept((Player) sender, player);
                                case "invite":
                                    duel_send((Player) sender, player);
                                case "deny":
                                    duel_deny((Player) sender, player);
                            }
                         } else {
                            sender.sendMessage("이미 누군가 플레이중입니다");
                        }
                        return true;
                    } else if (args[0].equals("accept") || args[0].equals("deny") || args[0].equals("invite")) {
                        sender.sendMessage("플레이어가 오프라인입니다!");
                        return true;
                    } else {
                        send_help(sender);
                        return true;
                    }
                } // 스위치 끝
            } else { //플레이어가 아닐시
                System.out.println("이 커맨드는 플레이어만 실행할수 있습니다!");
            }
        } // 이름이 듀얼일시
        return false;
    }
}