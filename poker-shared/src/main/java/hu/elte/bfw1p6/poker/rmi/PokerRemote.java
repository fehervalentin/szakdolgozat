package hu.elte.bfw1p6.poker.rmi;

import java.io.Serializable;
import java.math.BigDecimal;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

import hu.elte.bfw1p6.poker.client.observer.PokerRemoteObserver;
import hu.elte.bfw1p6.poker.command.PlayerCommand;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerInvalidPassword;
import hu.elte.bfw1p6.poker.exception.PokerInvalidUserException;
import hu.elte.bfw1p6.poker.exception.PokerTableDeleteException;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.exception.PokerUserBalanceException;
import hu.elte.bfw1p6.poker.model.PokerSession;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.model.entity.User;

/**
 * A szerver publikus interface-e.
 * @author feher
 *
 */
public interface PokerRemote extends Remote, Serializable {

	/**
	 * Regisztráció a póker játékba
	 * @param username a felhasználói név
	 * @param password a hozzáférés jelszava
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 */
	void registration(String username, String password) throws RemoteException, PokerDataBaseException;

	/**
	 * Adott felhasználói fiókhoz tartozó jelszó cseréje
	 * @param uuid a kliens egyedi session azonosítója
	 * @param oldPassword a régi jelszó
	 * @param newPassword az új jelszó
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 * @throws PokerInvalidPassword
	 */
	void modifyPassword(UUID uuid, String oldPassword, String newPassword) throws RemoteException, PokerDataBaseException, PokerInvalidPassword;
	
	/**
	 * Lekérdezi a kliens egyenlegét.
	 * @param uuid a kliens egyedi session azonosítója
	 * @return a kliens egyenlege
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 */
	BigDecimal refreshBalance(UUID uuid) throws RemoteException, PokerDataBaseException;
	
	/**
	 * Új játéktáblát hoz létre
	 * @param uuid a kliens egyedi session azonosítója
	 * @param t a létrehozandó tábla entitás
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 */
	void createTable(UUID uuid, PokerTable t) throws RemoteException, PokerDataBaseException;
	
	/**
	 * Már meglévő játéktáblát töröl
	 * @param uuid a kliens egyedi session azonosítója
	 * @param pokerTable a törlendő játéktábla
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 * @throws PokerTableDeleteException 
	 */
	void deleteTable(UUID uuid, PokerTable pokerTable) throws RemoteException, PokerDataBaseException, PokerTableDeleteException;

	/**
	 * Már meglévő játéktábla módosítása
	 * @param uuid a kliens egyedi session azonosítója
	 * @param t a módosítandó játéktábla
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 * @throws PokerTableDeleteException 
	 */
	void modifyTable(UUID uuid, PokerTable t) throws RemoteException, PokerDataBaseException, PokerTableDeleteException;

	/**
	 * Az összes játéktábla lekérése
	 * @param uuid a kliens egyedi session azonosítója
	 * @return az összes játéktábla
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 */
	List<PokerTable> getTables(UUID uuid) throws RemoteException, PokerDataBaseException;
	
	/**
	 * Bejelentkezés a póker játékba
	 * @param username a felhasználónév
	 * @param password a jelszó
	 * @throws RemoteException
	 * @throws PokerInvalidUserException
	 * @throws PokerDataBaseException
	 * @return PokerSession a kliensek számára egyedi UUID-val
	 */
	PokerSession login(String username, String password) throws RemoteException, PokerInvalidUserException, PokerDataBaseException;

	/**
	 * Kijelentkezés a póker játékból
	 * @param uuid a kliens egyedi session azonosítója
	 * @throws RemoteException
	 */
	void logout(UUID uuid) throws RemoteException;

	/**
	 * Lekérdezheti, hogy az adott felhasználó admin jogkörrel rendelkezik-e.
	 * @param uuid a kliens egyedi session azonosítója
	 * @return ha a felhasználó admin jogokkal rendelkezik, akkor true, különben false
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 */
	boolean isAdmin(UUID uuid) throws RemoteException, PokerDataBaseException;

	/**
	 * Az összes játéktáblát adja vissza.
	 * @param uuid a kliens egyedi session azonosítója
	 * @param observer a beregisztrálandó kliens
	 * @return az adatbázisban megtalálható összes játéktábla
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 */
	List<PokerTable> registerTableViewObserver(UUID uuid, PokerRemoteObserver observer) throws RemoteException, PokerDataBaseException;
	
	/**
	 * Utasítás küldése játékszervernek.
	 * @param uuid a kliens egyedi session azonosítója
	 * @param t a játéktábla, aminek utasítást szeretnénk küldeni
	 * @param client maga a kliens
	 * @param playerCommand az utasítás
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 * @throws PokerUserBalanceException
	 */
	void sendPlayerCommand(UUID uuid, PokerTable t, PokerRemoteObserver client, PlayerCommand playerCommand) throws RemoteException, PokerDataBaseException, PokerUserBalanceException;
	
	/**
	 * Asztalhoz való csatlakozási kérelem
	 * @param uuid a kliens egyedi session azonosítója
	 * @param t az asztal amihez csatlakozni szeretne a kliens
	 * @param observer a kliens
	 * @throws RemoteException
	 * @throws PokerTooMuchPlayerException
	 */
	void connectToTable(UUID uuid, PokerTable t, PokerRemoteObserver observer) throws RemoteException, PokerTooMuchPlayerException;

	/**
	 * Az összes regisztrált felhasználót kérdezi le.
	 * @param uuid a kliens egyedi session azonosítója
	 * @return az összes regiszrált felhasználó
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 */
	List<User> getUsers(UUID uuid) throws RemoteException, PokerDataBaseException;

	/**
	 * Lekérdezi, hogy a kiválasztott asztalnál van-e még szabad hely.
	 * @param uuid a kliens egyedi session azonosítója
	 * @param paramPokerTable a kiválasztott asztal
	 * @return ha van szabad hely, akkor true, különben false
	 * @throws RemoteException
	 */
	boolean canSitIn(UUID uuid, PokerTable paramPokerTable) throws RemoteException;

	/**
	 * Már meglévő felhasználó módosítása
	 * @param uuid a kliens egyedi session azonosítója
	 * @param u a módosítandó felhasználó
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 */
	void modifyUser(UUID id, User u) throws RemoteException, PokerDataBaseException;

	/**
	 * Már meglévő felhasználót töröl
	 * @param uuid a kliens egyedi session azonosítója
	 * @param u a törlendő felhasználó
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 */
	void deleteUser(UUID id, User u) throws RemoteException, PokerDataBaseException;
}