package hu.elte.bfw1p6.poker.command.type.enumeration;

import hu.elte.bfw1p6.poker.command.api.CommandTypeEnum;

public enum ClassicPlayerPokerCommandTypeEnum implements CommandTypeEnum {
	BLIND, CALL, CHECK, FOLD, RAISE, QUIT, CHANGE;
}
