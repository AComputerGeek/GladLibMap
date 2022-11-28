import edu.duke.*;
import java.util.*;

/**
* 
* @author: Amir Armion 
* 
* @version: V.01
* 
*/
public class GladLibMap 
{
    private HashMap<String, ArrayList<String>> myMap = new HashMap<>();
    private ArrayList<String> wordsUsed;

    private Random myRandom;

    private static String dataSourceURL       = "http://dukelearntoprogram.com/course3/data";
    private static String dataSourceDirectory = "data";

    public GladLibMap()
    {
        initializeFromSource(dataSourceDirectory);
        wordsUsed = new ArrayList<>();
        myRandom  = new Random();
    }

    public GladLibMap(String source)
    {
        initializeFromSource(source);
        wordsUsed = new ArrayList<>();
        myRandom  = new Random();
    }

    private void initializeFromSource(String source) 
    {     
        ArrayList<String> list   = new ArrayList<>();
        String[]          labels = {"adjective", "noun", "color", "country", "name", "animal", "timeframe", "verb", "fruit"};

        for(String label: labels)
        {
            list = readIt(source + "/" + label + ".txt");

            myMap.put(label, list);
        }
    }

    private ArrayList<String> readIt(String source)
    {
        ArrayList<String> list = new ArrayList<String>();

        if(source.startsWith("http")) 
        {
            URLResource resource = new URLResource(source);

            for(String line: resource.lines())
            {
                list.add(line);
            }
        }
        else 
        {
            FileResource resource = new FileResource(source);

            for(String line: resource.lines())
            {
                list.add(line);
            }
        }

        return list;
    }

    private String getSubstitute(String label) 
    {        
        if(label.equals("number"))
        {
            return "" + myRandom.nextInt(50) + 5;
        }

        return randomFrom(myMap.get(label));
    }

    private String randomFrom(ArrayList<String> source)
    {
        int index = myRandom.nextInt(source.size());

        return source.get(index);
    }

    private String processWord(String w)
    {
        int first = w.indexOf("<");
        int last  = w.indexOf(">", first);

        if(first == -1 || last == -1)
        {
            return w;
        }

        String prefix = w.substring(0, first);
        String suffix = w.substring(last + 1);
        String lable  = w.substring(first + 1, last);

        String sub    = getSubstitute(lable);

        while(wordsUsed.contains(sub))
        {
            sub = getSubstitute(lable);
        }

        wordsUsed.add(sub);

        return prefix + sub + suffix;
    }

    private String fromTemplate(String source)
    {
        String story = "";

        if(source.startsWith("http")) 
        {
            URLResource resource = new URLResource(source);

            for(String word: resource.words())
            {
                story = story + processWord(word) + " ";
            }
        }
        else 
        {
            FileResource resource = new FileResource(source);

            for(String word: resource.words())
            {
                story = story + processWord(word) + " ";
            }
        }

        return story;
    }

    private void printOut(String s, int lineWidth)
    {
        int charsWritten = 0;

        for(String w: s.split("\\s+"))
        {
            if(charsWritten + w.length() > lineWidth)
            {
                System.out.println();
                charsWritten = 0;
            }

            System.out.print(w + " ");
            charsWritten += w.length() + 1;
        }
    }

    public int totalWordsInMap()
    {
        int count = 0;

        for(ArrayList a: myMap.values())
        {
            count += myMap.values().size();
        }

        return count;
    }

    public int totalWordsConsidered()
    {
        ArrayList<String> wordCategoriesConsidered = new ArrayList<>();
        
        int count = 0;
        
        for(ArrayList a: myMap.values())
        {
            for(int i = 0; i < wordsUsed.size(); i++)
            {
                if(a.contains(wordsUsed.get(i)))
                {
                    for(String key: myMap.keySet())
                    {
                        if(!wordCategoriesConsidered.contains(key))
                        {
                            wordCategoriesConsidered.add(key);
                        }
                    }
                }
            }
        }
        
        for(String key: myMap.keySet())
        {
            for(int i = 0; i < wordCategoriesConsidered.size(); i++)
            {
                if(key.equals(wordCategoriesConsidered.get(i)))
                {
                    count += myMap.get(key).size();
                }
            }
        }

        return count;
    }

    public void makeStory()
    {
        wordsUsed.clear();

        String story = fromTemplate("data/madtemplate3.txt");

        printOut(story, 60);

        System.out.println("\n\n- The total number of words that were replaced: " + wordsUsed.size() + " words\n");

        System.out.println("\nThe total number of words that were possible to pick: " + totalWordsInMap());

        System.out.println("\nThe total number of words in the ArrayLists of the categories that were used for a particular: " + totalWordsConsidered());
    }
}
