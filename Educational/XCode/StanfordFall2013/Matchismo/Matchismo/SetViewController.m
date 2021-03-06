//
//  SetViewController.m
//  Matchismo
//
//  Created by Ethan Petuchowski on 12/5/13.
//  Copyright (c) 2013 Ethan Petuchowski. All rights reserved.
//

#import "SetViewController.h"
#import "SetCardDeck.h"
#import "SetGame.h"
#import "SetCard.h"

@interface SetViewController ()

@property (strong, nonatomic) SetGame *game;

#define SET_CARDS_TO_MATCH 3

@end

@implementation SetViewController

@synthesize game = _game;


- (SetGame *)game
{
    if (!_game) _game = [[SetGame alloc]
                         initWithCardCount:[self.cardButtons count]
                                 usingDeck:[self createDeck]
                           numCardsToMatch:SET_CARDS_TO_MATCH];
    return _game;
}


- (Deck *)createDeck
{
    return [[SetCardDeck alloc] init];
}


- (UIImage *)selectedCardImage:(Card *)card
{
    return [UIImage imageNamed:card.isChosen ? @"selectedcard" : @"cardfront"];
}


- (void)viewDidLoad
{
    [super viewDidLoad];
    [self updateUI];
}


- (void)updateUI
{
    for (UIButton *cardButton in self.cardButtons) {
        int cardButtonIndex = [self.cardButtons indexOfObject:cardButton];
        SetCard *card = (SetCard *)[self.game cardAtIndex:cardButtonIndex];
        [cardButton setAttributedTitle:[self attributedTitleForCard:card]
                    forState:UIControlStateNormal];
        [cardButton setBackgroundImage:[self selectedCardImage:card]
                              forState:UIControlStateNormal];
        cardButton.titleLabel.font = [UIFont systemFontOfSize:38];
        cardButton.enabled = !card.isMatched;
        self.scoreLabel.text =
        [NSString stringWithFormat:@"Score: %d", self.game.score];
    }
}

- (NSAttributedString *)attributedTitleForCard:(SetCard *)card
{
    return card.attributedContents;
}

@end
