package hu.elte.bfw1p6.poker.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import hu.elte.bfw1p6.poker.client.observer.RemoteObserver;
import hu.elte.bfw1p6.poker.client.observer.TableViewObserver;
import hu.elte.bfw1p6.poker.command.holdem.PlayerHoldemCommand;
import hu.elte.bfw1p6.poker.exception.PokerDataBaseException;
import hu.elte.bfw1p6.poker.exception.PokerInvalidPassword;
import hu.elte.bfw1p6.poker.exception.PokerInvalidUserException;
import hu.elte.bfw1p6.poker.exception.PokerTooMuchPlayerException;
import hu.elte.bfw1p6.poker.exception.PokerUnauthenticatedException;
import hu.elte.bfw1p6.poker.model.PokerSession;
import hu.elte.bfw1p6.poker.model.entity.Player;
import hu.elte.bfw1p6.poker.model.entity.PokerTable;

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
	 * @param pokerSession a kliens sessionje
	 * @param u a User, akit ki szeretnénk törölni
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 */
	void deletePlayer(PokerSession pokerSession, Player player) throws RemoteException, PokerDataBaseException;

	/**
	 * Adott felhasználói fiókhoz tartozó jelszó cseréje
	 * @param pokerSession a kliens sessionje
	 * @param username a felhasználói név
	 * @param oldPassword a régi jelszó
	 * @param newPassword az új jelszó
	 * @throws RemoteException
	 */
	void modifyPassword(PokerSession pokerSession, String oldPassword, String newPassword) throws RemoteException, PokerDataBaseException, PokerInvalidPassword, PokerUnauthenticatedException;
	
	
	
	
	/**
	 * Új játéktáblát hoz létre
	 * @param pokerSession a kliens sessionje
	 * @param t a létrehozandó tábla entitás
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 */
	void createTable(PokerSession pokerSession, PokerTable t) throws RemoteException, PokerDataBaseException, PokerUnauthenticatedException;
	
	/**
	 * Már meglévő játéktáblát töröl
	 * @param pokerSession a kliens sessionje
	 * @param pokerTable a törlendő játéktábla
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 */
	void deleteTable(PokerSession pokerSession, PokerTable pokerTable) throws RemoteException, PokerDataBaseException;

	/**
	 * Már meglévő játéktábla módosítása
	 * @param pokerSession a kliens sessionje
	 * @param t a módosítandó játéktábla
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 */
	void modifyTable(PokerSession pokerSession, PokerTable t) throws RemoteException, PokerDataBaseException;

	/**
	 * Az összes játéktábla lekérése
	 * @param pokerSession a kliens sessionje
	 * @return az összes játéktábla
	 * @throws RemoteException
	 * @throws PokerDataBaseException
	 */
	List<PokerTable> getTables(PokerSession pokerSession)throws RemoteException, PokerDataBaseException;
	
	
	
	

	void registerObserver(PokerSession pokerSession, RemoteObserver proc) throws RemoteException, PokerDataBaseException;

	void unRegisterObserver(PokerSession pokerSession, TableViewObserver proc) throws RemoteException;








	/**
	 * Bejelentkezés a póker játékba
	 * @param username a felhasználónév
	 * @param password a jelszó
	 * @param username
	 * @param password
	 * @return PokerSession a kliensek számára egyedi UUID-val
	 * @throws RemoteException
	 * @throws PokerInvalidUserException
	 * @throws PokerDataBaseException
	 */
	PokerSession login(String username, String password) throws RemoteException, PokerInvalidUserException, PokerDataBaseException;

	/**
	 * Kijelentkezés a póker játékból
	 * @param pokerSession a kliens sessionje
	 * @throws RemoteException
	 */
	void logout(PokerSession pokerSession) throws RemoteException;

	boolean shutDown(PokerSession pokerSession) throws RemoteException, PokerInvalidUserException;

	boolean isAdmin(PokerSession pokerSession) throws RemoteException;


	void addObserver(PokerSession pokerSession, RemoteObserver observer) throws RemoteException;

	List<PokerTable> registerTableViewObserver(PokerSession pokerSession, RemoteObserver observer) throws RemoteException, PokerDataBaseException;

	void removeTableViewObserver(RemoteObserver observer) throws RemoteException;
	
	
	
	
	void sendPlayerCommand(PokerSession pokerSession, PokerTable t, RemoteObserver client, PlayerHoldemCommand playerCommand) throws RemoteException;
	
	void connectToTable(PokerSession pokerSession, PokerTable t, RemoteObserver observer) throws RemoteException, PokerTooMuchPlayerException;
}
