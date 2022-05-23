package Splash;

import Calculos.Calculos_mujucoep;
import Funcoes.*;
import com.sun.javafx.application.LauncherImpl;
import entrada.Main;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.stage.Stage;

import java.util.Date;

public class SplashScreenProject extends Application {
    private DbMain conn = null;
    private static final int COUNT_LIMIT = 10;

    @Override
    public void start(Stage stage) throws Exception {
        Main.main(new String[] {"com.sibvisions.rad.ui.javafx.impl.JavaFXFactory"});
    }

    private static void LerSettings() {
        // Settings
        new Settings();
        VariaveisGlobais.LerConf();

        VariaveisGlobais.dbsenha = Boolean.valueOf(System.getProperty("dbSenha", "false"));
        VariaveisGlobais.dbnome  = System.getProperty("dbNome", "jgeralfx");
        VariaveisGlobais.unidade = System.getProperty("Unidade", "127.0.0.1");

        String[] _host = null;
        if (!"".equals(VariaveisGlobais.unidade)) {
            _host = VariaveisGlobais.unidade.split(",");
            if (_host.length > 1) {
                VariaveisGlobais.unidade = _host[0];
                VariaveisGlobais.dbnome = _host[1];
                VariaveisGlobais.dbsenha = Boolean.valueOf(_host[2]);
            }
        }

        if (!"".equals(VariaveisGlobais.unidade)) VariaveisGlobais.unidades = FuncoesGlobais.ObjectsAdd(VariaveisGlobais.unidades, new Object[]{VariaveisGlobais.unidade,VariaveisGlobais.dbnome,VariaveisGlobais.dbsenha});
        for (int w=1;w<=99;w++) {
            VariaveisGlobais.remoto1 = System.getProperty("remoto" + LerValor.FormatPattern(String.valueOf(w), "#0"), "");

            String[] _host1 = null;
            if (!"".equals(VariaveisGlobais.remoto1)) {
                _host1 = VariaveisGlobais.remoto1.split(",");
                if (_host1.length > 1) {
                    VariaveisGlobais.remoto1  = _host1[0];
                    VariaveisGlobais.dbnome1  = _host1[1];
                    VariaveisGlobais.dbsenha1 = Boolean.valueOf(_host1[2]);
                }
            }
            if (!"".equals(VariaveisGlobais.remoto1)) VariaveisGlobais.unidades = FuncoesGlobais.ObjectsAdd(VariaveisGlobais.unidades, new Object[]{VariaveisGlobais.remoto1,VariaveisGlobais.dbnome1,VariaveisGlobais.dbsenha1});
        }
    }

    @Override
    public void init() throws Exception {

        // Perform some heavy lifting (i.e. database start, check for application updates, etc. )
        for (int i = 1; i <= COUNT_LIMIT; i++) {
            double progress =(double) i/10;
            //System.out.println("progress: " +  progress);
            LauncherImpl.notifyPreloader(this, new Preloader.ProgressNotification(progress));
            switch (i) {
                case 1:
                    LerSettings();
                    conn = new DbMain(VariaveisGlobais.unidade,"postgres",(VariaveisGlobais.dbsenha ? "7kf51b" : ""),VariaveisGlobais.dbnome);
                    VariaveisGlobais.conexao = conn;
                    break;
                case 2:
                    new Calculos_mujucoep();
                    break;
                case 3:
                    new Calculos.Config().Config_ADM();
                    break;
                case 4:
                    new Calculos.Config().Config_AC();
                    break;
                case 5:
                    new Calculos.Config().Config_CA();
                    break;
                case 6:
                    new Calculos.Config().Config_BA();
                    break;
                case 7:
                    new Calculos.Config().Config_BB();
                    break;
                case 8:
                    new Calculos.Config().Config_Email();
                    break;
                case 9:
                    new Calculos.Config().Config_MsgProp();
                    break;
                case 10:
                    // Checa se é o primeiro dia do mes
//                    String datainic = null;
//                    try {
//                        datainic = conn.LerParametros("PARTIDA");
//                    } catch (Exception w) {}
//                    if (datainic != null) {
//                        Date dataserver = DbMain.getDateTimeServer();
//                        Date datapartid = Dates.StringtoDate(datainic,"yyyy-MM-dd");
//                        int difdate = Dates.DiffDate(dataserver, datapartid);
//                        if (difdate < 0) {
//                            // A data do servidor esta retroagindo. N�o � permitido
//                            System.out.println("Voce nao pode retroagir data.\n\nAcerte a data do sistema.");
//                            System.exit(1);
//                        } else if (difdate == 0) {
//                            // A data esta no dia
//                            conn.GravarParametros(new String[]{"PARTIDA", Dates.DateFormata("yyyy-MM-dd",dataserver), "DATE"});
//                        } else if (difdate > 0) {
//                            if (difdate <= 5) {
//                                // checa se tem algum feriado na semana e assume nova data
//                                Date datanova = Dates.DateAdd(Dates.DIA, difdate, datapartid);
//                                datanova = Dates.toDate(Dates.FimDeSemana(datanova));
//                                conn.GravarParametros(new String[]{"PARTIDA", Dates.DateFormata("yyyy-MM-dd",datanova), "DATE"});
//                            } else {
//                                System.out.println("Voce nao pode avan�ar a data em mais de 5(cinco) dias.\n\nAcerte a data do sistema.");
//                                System.exit(1);
//                            }
//                        }
//                    } else {
//                        // TODO - Acertar, não esta vendo a conecxao
//                        datainic = Dates.DateFormata("yyyy-MM-dd",DbMain.getDateTimeServer());
//                        conn.GravarParametros(new String[]{"PARTIDA",datainic,"DATE"});
//                    }
                    //conn.FecharConexao();
                    break;
            }
            Thread.sleep(100);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        LauncherImpl.launchApplication(SplashScreenProject.class, MyPreloader.class, args);
    }

}
