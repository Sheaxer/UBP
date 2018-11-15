//////////////////////////////////////////////////////////////////////////
// TODO:                                                                //
// Uloha2: Upravte funkciu na prihlasovanie tak, aby porovnavala        //
//         heslo ulozene v databaze s heslom od uzivatela po            //
//         potrebnych upravach.                                         //
// Uloha3: Vlozte do prihlasovania nejaku formu oneskorenia.            //
//////////////////////////////////////////////////////////////////////////
import java.io.IOException;
import java.util.StringTokenizer;

public class Login {
    protected static Database.MyResult prihlasovanie(String meno, String heslo) throws IOException, Exception{
        /*
        *   Delay je vhodne vytvorit este pred kontolou prihlasovacieho mena.
        */
        Database.MyResult account = Database.find("hesla.txt", meno);
        if (!account.getFirst()){
            return new Database.MyResult(false, "Nespravne meno.");
        }
        else {
            StringTokenizer st = new StringTokenizer(account.getSecond(), ":");
            st.nextToken();      //prvy token je prihlasovacie meno
            /*
            *   Pred porovanim hesiel je nutne k heslu zadanemu od uzivatela pridat prislusny salt z databazy a nasledne tento retazec zahashovat.
            */
            boolean rightPassword = st.nextToken().equals(heslo);
            if (!rightPassword)    
                return new Database.MyResult(false, "Nespravne heslo.");
        }
        return new Database.MyResult(true, "Uspesne prihlasenie.");
    }
}
