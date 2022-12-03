package exampleMapping;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import java.util.ArrayList;
import java.util.List;
import org.gaas.kuhhandel.bean.Card;
import org.gaas.kuhhandel.bean.HandCard;
import org.gaas.kuhhandel.bean.MoneyCard;
import org.gaas.kuhhandel.eum.Animal;
import org.gaas.kuhhandel.repository.InitiateTrader;
import org.gaas.kuhhandel.repository.Respondent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class kuhhandel {

	InitiateTrader initiateTrader;
	Respondent respondent;
	HandCard initiateTraderHandCard = new HandCard();
	List<Card> initiateTraderCards = new ArrayList<Card>();
	List<MoneyCard> initiateTraderMoneyCards = new ArrayList<MoneyCard>();
	
	HandCard respondentHandCard = new HandCard();
	List<Card> respondentCards = new ArrayList<Card>();
	List<MoneyCard> respondentMoneyCards = new ArrayList<MoneyCard>();
	
    @Before
    public void init() {
        initiateTrader = Mockito.mock(InitiateTrader.class);
        respondent = Mockito.mock(Respondent.class);
        
    	// 發起交易者手牌
    	initiateTraderCards = new ArrayList<Card>();
    	Card iTcard1 = new Card(Animal.DONKEY,1);
    	Card iTcard2 = new Card(Animal.CATTLE,1);
    	initiateTraderCards.add(iTcard1);
    	initiateTraderCards.add(iTcard2);
    	initiateTraderMoneyCards = new ArrayList<MoneyCard>();
    	MoneyCard iTmoneyCard1 = new MoneyCard(100,2);
    	MoneyCard iTmoneyCard2 = new MoneyCard(200,1);
    	initiateTraderMoneyCards.add(iTmoneyCard1);
    	initiateTraderMoneyCards.add(iTmoneyCard2);
    	initiateTraderHandCard = new HandCard(1, "發起交易者", initiateTraderCards, initiateTraderMoneyCards);
    	
    	// 回答者手牌
    	respondentCards = new ArrayList<Card>();
    	Card resCard1 = new Card(Animal.DONKEY,1);
    	Card resCard2 = new Card(Animal.GOAT,2);
    	respondentCards.add(resCard1);
    	respondentCards.add(resCard2);
    	respondentMoneyCards = new ArrayList<MoneyCard>();
    	MoneyCard resMoneyCard1 = new MoneyCard(50,2);
    	MoneyCard resMoneyCard2 = new MoneyCard(200,1);
    	respondentMoneyCards.add(resMoneyCard1);
    	respondentMoneyCards.add(resMoneyCard2);
    	respondentHandCard = new HandCard(2, "回答者", respondentCards, respondentMoneyCards);

    }

    // 接受出價
    @Test
    public void givenSelectCardAndBid_whenAcceptBid_thenInitiateTraderGetCardFromRespondent() {

    	
    	Card card = new Card(Animal.DONKEY,1);
    	List<MoneyCard> moneyCards = new ArrayList<MoneyCard>();
    	MoneyCard moneyCard = new MoneyCard(100,1);
    	moneyCards.add(moneyCard);
        given(initiateTrader.selectCard(card));
        given(initiateTrader.bid(moneyCards));

        then(respondent).should().acceptBid(moneyCards);
        respondentMoneyCards.addAll(moneyCards);
        respondentCards.remove(card);
        respondentHandCard.setMoneyCards(respondentMoneyCards);
        respondentHandCard.setAnimalCard(respondentCards);
        then(respondent).should().changeHandCard(respondentHandCard);
       
        initiateTraderMoneyCards.remove(moneyCards);
        initiateTraderCards.add(0, card);
        initiateTraderHandCard.setMoneyCards(initiateTraderMoneyCards);
        initiateTraderHandCard.setAnimalCard(initiateTraderCards);
        then(initiateTrader).should().changeHandCard(initiateTraderHandCard);

    }
}
