package hu.elte.bfw1p6.poker.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

import hu.elte.bfw1p6.poker.client.observer.RemoteObserver;
import hu.elte.bfw1p6.poker.client.observer.TableViewObserver;
import hu.elte.bfw1p6.poker.command.holdem.PlayerHoldemCommand;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerInvalidUserException;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.model.entity.Player;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;
import hu.elte.bfw1p6.poker.model.entity.User;

public interface PokerRemote extends Remote {

	/**
	 * Regisztráció a póker játékba
	 * @param username a felhasználói név
	 * @param password a hozzáférés jelszava
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 */
	void registration(String username, String password) throws RemoteException, PokerDataBaseException;
	
	/**
	 * Felhasználói hozzáférés törlése
	 * @param uuid a kliens azonosítója
	 * @param u a User, akit ki szeretnénk törölni
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 */
	void deleteUser(UUID uuid, User u) throws RemoteException, PokerDataBaseException;

	/**
	 * Adott felhasználói fiókhoz tartozó jelszó cseréje
	 * @param uuid a kliens azonosítója
	 * @param username a felhasználói név
	 * @param oldPassword a régi jelszó
	 * @param newPassword az új jelszó
	 * @throws RemoteException
	 */
	void modifyPassword(UUID uuid, String username, String oldPassword, String newPassword) throws RemoteException;
	
	
	
	
	/**
	 * Új játéktáblát hoz létre
	 * @param uuid a kliens azonosítója
	 * @param t a létrehozandó tábla entitás
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 */
	void createTable(UUID uuid, PokerTable t) throws RemoteException, PokerDataBaseException;
	
	/**
	 * Már meglévő játéktáblát töröl
	 * @param uuid a kliens azonosítója
	 * @param pokerTable a törlendő játéktábla
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 */
	void deleteTable(UUID uuid, PokerTable pokerTable) throws RemoteException, PokerDataBaseException;

	/**
	 * Már meglévő játéktábla módosítása
	 * @param uuid a kliens azonosítója
	 * @param t a módosítandó játéktábla
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 */
	void modifyTable(UUID uuid, PokerTable t) throws RemoteException, PokerDataBaseException;

	/**
	 * Az összes játéktábla lekérése
	 * @param uuid a kliens azonosítója
	 * @return az összes játéktábla
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 */
	List<PokerTable> getTables(UUID uuid)throws RemoteException, PokerDataBaseException;
	
	
	
	

	void registerObserver(UUID uuid, RemoteObserver proc) throws RemoteException, PokerDataBaseException;

	void unRegisterObserver(UUID uuid, TableViewObserver proc) throws RemoteException;








	/**
	 * Bejelentkezés a póker játékba
	 * @param username a felhasználónév
	 * @param password a jelszó
	 * @return egyedi UUID azonosító a kliensek számára
	 * @throws RemoteException
	 * @throws PokerInvalidUserException
	 */
	UUID login(String username, String password) throws RemoteException, PokerInvalidUserException;

	/**
	 * Kijelentkezés a póker játékból
	 * @param uuid a kliens azonosítója
	 * @throws RemoteException
	 */
	void logout(UUID uuid) throws RemoteException;

	boolean shutDown(UUID uuid) throws RemoteException, PokerInvalidUserException;

	boolean isAdmin(UUID uuid) throws RemoteException;


	void addObserver(UUID uuid, RemoteObserver observer) throws RemoteException;

	List<PokerTable> registerTableViewObserver(UUID uuid, RemoteObserver observer) throws RemoteException, PokerDataBaseException;

	void removeTableViewObserver(RemoteObserver observer) throws RemoteException;
	
	
	
	
	void sendPlayerCommand(UUID uuid, PokerTable t, RemoteObserver client, PlayerHoldemCommand playerCommand) throws RemoteException;
	
	void connectToTable(UUID uuid, PokerTable t, RemoteObserver observer) throws RemoteException, PokerTooMuchPlayerException;
}
