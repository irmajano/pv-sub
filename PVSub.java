/**
 * PVSub
 *
 * @author  Ignacio Rubio Majano, Maryam Geranmayeh, Cornelius Fath
 * @todo Set WordNet directory
 */

import java.util.*;
import java.lang.*;

import edu.smu.tspell.wordnet.*;

import com.google.common.collect.Sets;

public class PVSub {
    public static void main(String[] args) {
        // Set WordNet directory: replace "location"
        System.setProperty("wordnet.database.dir",
                "location");

        NounSynset nounSynset;
        NounSynset[] hyponyms;
        List<Synset[]> senseList = new ArrayList<>();
        List<List<Synset>> senseCombinations = null;
        int n = 0;
        int best_score_till_now = 0;
        List<Synset> best_candidate_till_now = new ArrayList<>();

        WordNetDatabase database = WordNetDatabase.getFileInstance();

        System.out.println("Hello world");
        String d1 = "dog";
        Synset[] d = database.getSynsets(d1);
        System.out.println(d[0].getType().equals(SynsetType.NOUN));
        List<String> test = relDefs(d[0]);
        for (String s : test) {
            System.out.println(s);
        }

        String[] words = {"sentence", "bench", "officer"};
        n = words.length;
        for (String word : words) {
            Synset[] synsets = database.getSynsets(word);
            List<Synset> onlynouns = new ArrayList<>();
            for (Synset s : synsets) {
                if (s.getType().equals(SynsetType.NOUN)) {
                    onlynouns.add(s);
                }
            }
            senseList.add(synsets);
        }

        senseCombinations = cartesianProduct(senseList);


        for (List<Synset> l : senseCombinations) {
            for (Synset[] list : senseList) {
                int combination_score = 0;

                for (int i = 0; i < n - 1; i++) {
                    List<String> reli = relDefs(list[i]);
                    for (int j = i + 1; j < n; j++) {
                        List<String> relj = relDefs(list[j]);

                        for (String si : reli) {
                            for (String sj : relj) {
                                combination_score = lcb(si, sj, "1", "0");
                            }
                        }
                    }
                }

                if (combination_score > best_score_till_now) {
                    best_score_till_now = combination_score;
                    best_candidate_till_now = l;
                }
            }
        }
        for (Synset s : best_candidate_till_now) {
            System.out.println(s.toString());
        }

    }

    public static List<List<Synset>> cartesianProduct(List<Synset[]> lists) {
        List<List<Synset>> resultLists = new ArrayList<>();
        if (lists.size() == 0) {
            resultLists.add(new ArrayList<Synset>());
            return resultLists;
        } else {
            Synset[] firstList = lists.get(0);
            List<List<Synset>> remainingLists = cartesianProduct(lists.subList(
                    1, lists.size()));
            for (Synset condition : firstList) {
                for (List<Synset> remainingList : remainingLists) {
                    List<Synset> resultList = new ArrayList<>();
                    resultList.add(condition);
                    resultList.addAll(remainingList);
                    resultLists.add(resultList);
                }
            }
        }
        return resultLists;
    }

    public static List<String> relDefs(Synset synset) {
        List<String> related = new ArrayList<>();
        related.add(synset.getDefinition());

        if (synset.getType().equals(SynsetType.NOUN)) {

            if (((NounSynset) synset).getHyponyms() != null) {
                String hypo = "";
                NounSynset[] hypoArray = (((NounSynset) synset).getHyponyms());
                for (NounSynset n : hypoArray) {
                    hypo += n.getDefinition() + " ";
                }
                related.add("hypo " + hypo.trim());
            }

            if (((NounSynset) synset).getHypernyms() != null) {
                String hyper = "";
                NounSynset[] hyperArray = (((NounSynset) synset).getHypernyms());
                for (NounSynset n : hyperArray) {
                    hyper += n.getDefinition() + " ";
                }
                related.add("hyper " + hyper.trim());
            }

            attrloop:
            if (((NounSynset) synset).getAttributes() != null) {
                String attr = "";
                AdjectiveSynset[] attrArray = (((NounSynset) synset)
                        .getAttributes());
                for (AdjectiveSynset a : attrArray) {
                    attr += a.getDefinition() + " ";
                }
                if (attr.isEmpty()) {
                    break attrloop;
                }
                related.add("attr " + attr.trim());
            }

            antoloop:
            if (synset.getAntonyms(synset.toString()) != null) {
                String anto = "";
                WordSense[] antoArray = synset.getAntonyms(synset.toString());
                for (WordSense n : antoArray) {
                    anto += n.getSynset().getDefinition() + " ";
                }
                if (anto.isEmpty()) {
                    break antoloop;
                }
                related.add(anto.trim());
            }

            if ((((NounSynset) synset).getMemberHolonyms() != null)
                    || (((NounSynset) synset).getPartHolonyms() != null)
                    || (((NounSynset) synset).getSubstanceHolonyms() != null)) {
                String holo = "";
                NounSynset[] holoArray = (((NounSynset) synset)
                        .getMemberHolonyms());
                for (NounSynset n : holoArray) {
                    holo += n.getDefinition() + " ";
                }
                holoArray = (((NounSynset) synset).getPartHolonyms());
                for (NounSynset n : holoArray) {
                    holo += n.getDefinition() + " ";
                }
                holoArray = (((NounSynset) synset).getSubstanceHolonyms());
                for (NounSynset n : holoArray) {
                    holo += n.getDefinition() + " ";
                }
                related.add(holo.trim());
            }

            if ((((NounSynset) synset).getMemberMeronyms() != null)
                    || (((NounSynset) synset).getPartMeronyms() != null)
                    || (((NounSynset) synset).getSubstanceMeronyms() != null)) {
                String mero = "";
                NounSynset[] meroArray = (((NounSynset) synset)
                        .getMemberMeronyms());
                for (NounSynset n : meroArray) {
                    mero += n.getDefinition() + " ";
                }
                meroArray = (((NounSynset) synset).getPartMeronyms());
                for (NounSynset n : meroArray) {
                    mero += n.getDefinition() + " ";
                }
                meroArray = (((NounSynset) synset).getSubstanceMeronyms());
                for (NounSynset n : meroArray) {
                    mero += n.getDefinition() + " ";
                }
                related.add(mero.trim());
            }
        }

        if (synset.getType().equals(SynsetType.VERB)) {

            if (((VerbSynset) synset).getTroponyms() != null) {
                String trop = "";
                VerbSynset[] tropArray = (((VerbSynset) synset).getTroponyms());
                for (VerbSynset v : tropArray) {
                    trop += v.getDefinition() + " ";
                }
                related.add(trop.trim());
            }

            if (((VerbSynset) synset).getHypernyms() != null) {
                String hype = "";
                VerbSynset[] hypeArray = (((VerbSynset) synset).getHypernyms());
                for (VerbSynset v : hypeArray) {
                    hype += v.getDefinition() + " ";
                }
                related.add(hype.trim());
            }

            if (synset.getAntonyms(synset.toString()) != null) {
                String anto = "";
                WordSense[] antoArray = synset.getAntonyms(synset.toString());
                for (WordSense v : antoArray) {
                    anto += v.getSynset().getDefinition() + " ";
                }
                related.add(anto.trim());
            }

            if (((VerbSynset) synset).getEntailments() != null) {
                String ent = "";
                VerbSynset[] entArray = (((VerbSynset) synset).getEntailments());
                for (VerbSynset v : entArray) {
                    ent += v.getDefinition() + " ";
                }
                related.add(ent.trim());
            }

            if (((VerbSynset) synset).getOutcomes() != null) {
                String cause = "";
                VerbSynset[] causeArray = (((VerbSynset) synset).getOutcomes());
                for (VerbSynset v : causeArray) {
                    cause += v.getDefinition() + " ";
                }
                related.add(cause.trim());
            }
        }

        if (synset.getType().equals(SynsetType.ADJECTIVE)) {

            if (((AdjectiveSynset) synset).getPertainyms(synset.toString()) != null) {
                String pert = "";
                WordSense[] pertArray = ((AdjectiveSynset) synset)
                        .getPertainyms(synset.toString());
                for (WordSense a : pertArray) {
                    pert += a.getSynset().getDefinition() + " ";
                }
                related.add(pert.trim());
            }

            if (synset.getAntonyms(synset.toString()) != null) {
                String anto = "";
                WordSense[] antoArray = synset.getAntonyms(synset.toString());
                for (WordSense a : antoArray) {
                    anto += a.getSynset().getDefinition() + " ";
                }
                related.add(anto.trim());
            }

            if (((AdjectiveSynset) synset).getSimilar() != null) {
                String sim = "";
                AdjectiveSynset[] simArray = (((AdjectiveSynset) synset)
                        .getSimilar());
                for (AdjectiveSynset a : simArray) {
                    sim += a.getDefinition() + " ";
                }
                related.add(sim.trim());
            }

            if (((AdjectiveSynset) synset).getRelated() != null) {
                String rel = "";
                AdjectiveSynset[] relArray = (((AdjectiveSynset) synset)
                        .getRelated());
                for (AdjectiveSynset a : relArray) {
                    rel += a.getDefinition() + " ";
                }
                related.add(rel.trim());
            }
        }

        if (synset.getType().equals(SynsetType.ADVERB)) {

            if (((AdverbSynset) synset).getPertainyms(synset.toString()) != null) {
                String pert = "";
                WordSense[] pertArray = ((AdverbSynset) synset)
                        .getPertainyms(synset.toString());
                for (WordSense a : pertArray) {
                    pert += a.getSynset().getDefinition() + " ";
                }
                related.add(pert.trim());
            }

            if (synset.getAntonyms(synset.toString()) != null) {
                String anto = "";
                WordSense[] antoArray = synset.getAntonyms(synset.toString());
                for (WordSense a : antoArray) {
                    anto += a.getSynset().getDefinition() + " ";
                }
                related.add(anto.trim());
            }
        }
        return related;
    }

    private static int lcb(String s1, String s2, String c, String sc) {
        System.setProperty("wordnet.database.dir",
                "/home/millue/Documents/WordNet-3.0/dict");
        WordNetDatabase database = WordNetDatabase.getFileInstance();

        System.out.println("SENTENCE 1: " + s1);
        System.out.println("SENTENCE 2: " + s2);

        List<String> split1 = new ArrayList<>(Arrays.asList(s1.split("\\s+")));
        List<String> split2 = new ArrayList<>(Arrays.asList(s2.split("\\s+")));

		/*
         * Lemmatizing nouns in input
		 */
        System.out.println("Changes in sentence 1:");
        for (int i = 0; i < split1.size(); i++) {
            Synset[] synsets = database.getSynsets(split1.get(i));
            for (Synset s : synsets) {
                if (s.getType().equals(SynsetType.NOUN)) {
                    String[] lemma = database.getBaseFormCandidates(
                            split1.get(i), SynsetType.NOUN);
                    if (lemma.length != 0) {
                        System.out.println(split1.get(i) + " -> " + lemma[0]);
                        split1.set(i, lemma[0]);
                    }
                    break;
                }
            }
        }
        System.out.println("\nChanges in sentence 2:");
        for (int i = 0; i < split2.size(); i++) {
            Synset[] synsets = database.getSynsets(split2.get(i));
            for (Synset s : synsets) {
                if (s.getType().equals(SynsetType.NOUN)) {
                    String[] lemma = database.getBaseFormCandidates(
                            split2.get(i), SynsetType.NOUN);
                    if (lemma.length != 0) {
                        System.out.println(split2.get(i) + " -> " + lemma[0]);
                        split2.set(i, lemma[0]);
                    }
                    break;
                }
            }
        }

        int counter = Integer.parseInt(c);
        int start = 0;
        int max = 0;
        int score = Integer.parseInt(sc);

        for (int i = 0; i < split1.size(); i++) {

			/*
             * Checking that first word is content-word
			 */
            Synset[] synsets = database.getSynsets(split1.get(i));
            boolean condition = true;
            System.out.println("***First word*** " + split1.get(i));
            if (synsets.length < 1) {
                System.out.println("...has no synsets");
                condition = false;
            } else {
                System.out.println("...has synsets");
                forloop:
                for (Synset s : synsets) {
                    if (s.getType().equals(SynsetType.ADVERB)) {
                        System.out.println("...is adverb");
                        condition = false;
                        break forloop;
                    }
                    if (s.getType().equals(SynsetType.NOUN)) {
                        String[] spellings = s.getWordForms();
                        for (String spelling : spellings) {
                            if (split1.get(i).equalsIgnoreCase(spelling)) {
                                if (Character.isUpperCase(spelling.charAt(0))) {
                                    System.out.println("...is proper noun");
                                    condition = false;
                                    break forloop;
                                }
                            }
                        }
                    }
                }
            }

            if (condition) {
                if (synsets.length != 0) {
                    for (int j = 0; j < split2.size(); j++) {
                        int x = 0;
                        while (split1.get(i + x).equals(split2.get(j + x))) {
                            System.out.println("MATCH: " + split1.get(i + x));
                            x++;
                            if (((i + x) >= split1.size())
                                    || ((j + x) >= split2.size())) {
                                break;
                            }
                        }
                        if (x > max) {
                            max = x;
                            start = i;
                        }
                    }
                }
            } else {
                System.out.println("...non-content word");
            }
        }

        if (max != 0) {
            List<String> overlap = new ArrayList<>();
            for (int i = start; i < start + max; i++) {
                overlap.add(split1.get(i));
            }

            System.out.println("Overlap:");
            for (String s : overlap) {
                System.out.println("\"" + s + "\"");
            }

			/*
			 * Checking that last word is content-word
			 */
            System.out.println("\nChecking last word:");
            while (true) {
                if (overlap.size() > 0) {
                    String lastWord = overlap.get(overlap.size() - 1);
                    System.out.println(lastWord);
                    Synset[] synsets = database.getSynsets(lastWord);
                    if (synsets.length < 1) {
                        System.out.println("...has no synsets");
                        overlap.remove(overlap.size() - 1);
                    } else {
                        boolean condition = true;
                        forloop:
                        for (Synset synset : synsets) {
                            if (synset.getType().equals(SynsetType.ADVERB)) {
                                System.out.println("...is adverb");
                                condition = false;
                                break forloop;
                            }
                            if (synset.getType().equals(SynsetType.NOUN)) {
                                System.out.println("...is noun");
                                String[] spellings = synset.getWordForms();
                                for (String s : spellings) {
                                    if (lastWord.equalsIgnoreCase(s)) {
                                        if (Character.isUpperCase(s.charAt(0))) {
                                            System.out
                                                    .println("...is proper noun");
                                            condition = false;
                                            break forloop;
                                        }
                                    }
                                }
                            }
                        }
                        for (Synset s : synsets) {
                            if (s.getType().equals(SynsetType.ADVERB)) {
                                System.out.println("...is adverb");
                                condition = false;
                            }
                        }
                        if (condition == false) {
                            System.out.println("...non-content word");
                            overlap.remove(overlap.size() - 1);
                        } else {
                            break;
                        }
                    }
                }
            }

			/*
			 * Get overlap as a string
			 */
            String overlapString = "";
            for (String s : overlap) {
                overlapString += s + " ";
            }
            overlapString = overlapString.trim();

			/*
			 * Replace overlap with markers in each sentence
			 */
            String m1 = "";
            for (String a : split1) {
                m1 += a + " ";
            }
            m1 = m1.replace(overlapString, "M" + counter);

            counter++;
            String m2 = "";
            for (String a : split2) {
                m2 += a + " ";
            }
            m2 = m2.replace(overlapString, "M" + counter);
            counter++;

            score += Math.pow(overlap.size(), 2);

            System.out.println("\n*****\n*****overlap: " + overlapString);
            System.out.println("m1: " + m1);
            System.out.println("m2: " + m2);
            System.out.println("max: " + overlap.size());
            System.out.println("score so far " + score + "\n*****\n*****\n");

            lcb(m1.trim(), m2.trim(), Integer.toString(counter),
                    Integer.toString(score));
        }
        return score;
    }


}