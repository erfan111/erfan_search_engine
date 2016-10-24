package com.company;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by erfan on 9/24/14.
 */
class inverted_index {
    private static HashMap<String,LinkedList> thedictionary = new HashMap<>();
    private static HashMap<String,Double[]> weights = new HashMap<>();
    private static String[] files;
    private static HashMap<String,Integer> doc_id = new HashMap<>();
    private static HashMap<Integer,String> inverse_doc_id = new HashMap<>();
    private static Integer N;


    // Checks if the given string is a stop word
    static private boolean is_stopword(String s){
        String[] stopwords ={"a", "about", "above", "above", "across", "after", "afterwards", "again", "against", "all", "almost",
                "alone", "along", "already", "also","although","always","am","among", "amongst", "amoungst", "amount",  "an", "and",
                "another", "any","anyhow","anyone","anything","anyway", "anywhere", "are", "around", "as",  "at", "back","be","became",
                "because","become","becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides",
                "between", "beyond", "bill", "both", "bottom","but", "by", "call", "can", "cannot", "cant", "co", "con", "could", "couldnt",
                "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven","else",
                "elsewhere", "empty", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few",
                "fifteen", "fify", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty", "found", "four", "from",
                "front", "full", "further", "get", "give", "go", "had", "has", "hasnt",
                "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself",
                "him", "himself", "his", "how", "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest", "into",
                "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many",
                "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must",
                "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none",
                "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto",
                "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own","part", "per", "perhaps",
                "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she",
                "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something",
                "sometime", "sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the", "their",
                "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon",
                "these", "they", "thickv", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru",
                "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until",
                "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever",
                "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while",
                "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet",
                "you", "your", "yours", "yourself", "yourselves","1","2","3","4","5","6","7","8","9","10","1.","2.","3.","4.","5.","6.","11",
                "7.","8.","9.","12","13","14","A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z",
                "terms","CONDITIONS","conditions","values","interested.","care","sure",".","!","@","#","$","%","^","&","*","(",")","{","}","[","]",":",";",",","<",".",">","/","?","_","-","+","=",
                "a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z",
                "contact","grounds","buyers","tried","said,","plan","value","principle.","forces","sent:","is,","was","like",
                "discussion","tmus","diffrent.","layout","area.","thanks","thankyou","hello","bye","rise","fell","fall","psqft.","http://","km","miles"};
        for(String word : stopwords){
            if(s.equals(word))
            {
                return true;
            }
        }
        return false;
    }

    // The starter method of inverted index, automatically creates the index from files in ../input/
    public static void read() throws IOException {
        File file = new File("/home/erfan/Documents/programming/invertedIndex/dataset/");
        Stemmer stemmer = new Stemmer();
        Integer master_counter;
        if(file.isDirectory()) {
            files = file.list();
            assert files != null;
            N = files.length;
            Integer n = 0;
            for (String ff : files) {
                doc_id.put(ff, n);
                inverse_doc_id.put(n, ff);
                n++;
            }
            System.out.println("Reading files:");
            for (int j = 0; j < N && Runtime.getRuntime().freeMemory() > 1000; j++)
                try {
                    System.out.println(files[j]);
                    System.out.println(j);
                    System.out.println("----------------------");
                    FileReader fReader = new FileReader("/home/erfan/Documents/programming/invertedIndex/dataset/" + files[j]);
                    BufferedReader br = new BufferedReader(fReader);
                    String line;
                    master_counter = 0;
                    while ((line = br.readLine()) != null) {
                        String[] input_array = line.split("[^\\d^\\w]");
                        for (String anInput_array : input_array) {
                            String final_word = anInput_array.toLowerCase();
                            char[] fw = final_word.toCharArray();
                            stemmer.add(fw, fw.length);
                            stemmer.stem();
                            final_word = stemmer.toString();
                            if (!final_word.isEmpty()) {
                                master_counter++;
                                if (!is_stopword(final_word)) {
                                    if (thedictionary.containsKey(final_word)) {
                                        LinkedList<Tuple<Integer, Integer, ArrayList<Integer>>> ll = thedictionary.get(final_word);
                                        boolean flag1 = false;
                                        for (Tuple<Integer, Integer, ArrayList<Integer>> aRecord : ll) { // check if occurred in that document before
                                            if (aRecord.x.equals(doc_id.get(files[j]))) {
                                                aRecord.y++;
                                                aRecord.z.add(master_counter);
                                                flag1 = true;
                                                break;
                                            }
                                        }
                                        if (!flag1) {
                                            ArrayList<Integer> pos_list = new ArrayList<>();
                                            pos_list.add(master_counter);
                                            ll.addFirst(new Tuple(doc_id.get(files[j]), 1, pos_list));

                                        }
                                    } else {
                                        ArrayList<Integer> pos_list = new ArrayList<>();
                                        pos_list.add(master_counter);
                                        LinkedList<Tuple<Integer, Integer, ArrayList<Integer>>> posting_list = new LinkedList<>();
                                        posting_list.addFirst(new Tuple<>(doc_id.get(files[j]), 1, pos_list));
                                        thedictionary.put(final_word, posting_list);
                                    }
                                }
                            }
                        }

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            boolean disk_flag = false;
            //noinspection ConstantConditions
            if(disk_flag){
                disk_handler();
            }
        }
        else{
            System.out.println("Please give a directory name.");
        }
        // calculate the weights
        for (String word : thedictionary.keySet()) {
            LinkedList ll = thedictionary.get(word);
            Double[] word_weights = new Double[N+1];
            for (Tuple<Integer,Integer,ArrayList> tpl : (Iterable<Tuple>) ll) {
                word_weights[tpl.x] = (double) tpl.y * Math.log10((double) N / (double) ll.size());
            }
            word_weights[0] = 0.00;
            for(int j=0;j<N+1;j++){
                if(word_weights[j] == null) word_weights[j] = 0.00;
            }
            weights.put(word, word_weights);
        }

//        System.out.println(thedictionary.get("ball"));
//        System.out.println(doc_id);


    }

    static void query(boolean weighted_flag, boolean new_method) throws IOException {
        read();
        System.out.print("Please input your query: ");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        String[] user_query = input.split("[^\\d^\\w]");
        String[] stemmed_query = new String[user_query.length];
        Stemmer stemmer = new Stemmer();
        Comparator<RankTuple> comparator = new TupleComparator();
        PriorityQueue<RankTuple> ranklist;
        // for each term in user's query:
        for(Integer qi=0; qi < user_query.length;qi++){
            char[] charterm = user_query[qi].toLowerCase().toCharArray();
            stemmer.add(charterm, charterm.length);
            stemmer.stem();
            stemmed_query[qi] = stemmer.toString();
        }
        if(new_method) {
            ranklist = calc_rank_ratio(stemmed_query, comparator);
//            RankTuple temp;
//            Stack<RankTuple> output = new Stack<>();
            if(ranklist.size() <10) {
                for (int j = 1; j <= ranklist.size(); j++) {
                    System.out.println(j + ") " + inverse_doc_id.get(ranklist.poll().x));
                }
//                for (int j = 1; j <= ranklist.size(); j++) {
//                    temp = output.pop();
//                    if(temp.y != 0)
//                        System.out.println(j + ") " + inverse_doc_id.get(temp.x);
//                }
            }
            else{
                for (int j = 1; j <= 10; j++) {
                    System.out.println(j + ") " + inverse_doc_id.get(ranklist.poll().x));
                }
//                for (int j = 1; j <= 10; j++) {
//                    temp = output.pop();
//                    if(temp.y != 0)
//                        System.out.println(j + ") " + inverse_doc_id.get(temp.x) + " " + files[temp.x]);
//                }

            }
        } else {
            ranklist = calc_rank(stemmed_query, comparator, weighted_flag);
            RankTuple temp;
            Stack<RankTuple> output = new Stack<>();
            if(N<10) {
                for (int j = 1; j <= N; j++) {
                    output.push(ranklist.poll());
                }
                for (int j = 1; j <= N; j++) {
                    temp = output.pop();
                    if(temp.y != 0)
                        System.out.println(j + ") " + files[temp.x]);
                }
            }
            else{
                for (int j = 1; j <= N; j++) {
                    output.push(ranklist.poll());
                }
                for (int j = 1; j <= 10; j++) {
                    temp = output.pop();
                    if(temp.y != 0)
                        System.out.println(j + ") " + files[temp.x]);
                }

            }
        }

    }

    private static PriorityQueue<RankTuple> calc_rank(String[] q, Comparator tc, boolean weighted_query){
        Double q_term_size = 1.00;
        PriorityQueue<RankTuple> result = new PriorityQueue<>(N,tc);
        for(int doc=1; doc < N+1; doc++){
            Double qdotd = 0.00;
            if(weighted_query) {
                for (int queryTermIndex = 0; queryTermIndex < q.length; queryTermIndex++) {
                    Double[] word_w = weights.get(q[queryTermIndex]);
                    if (word_w != null) qdotd += (q.length - queryTermIndex) * word_w[doc];
                }
            } else {
                for (String aQ : q) {
                    Double[] word_w = weights.get(aQ);
                    if (word_w != null) qdotd +=  word_w[doc];
                }
            }
            Double sigmad2 = 0.00;
            for(Double[] dbl : weights.values()){
                sigmad2 += (dbl[doc]*dbl[doc]);
            }
            if(weighted_query) {
                Double sigmaQ = 0.00;
                for (int qn = 0; qn < q.length; qn++) {
                    sigmaQ += qn * qn;
                }
                q_term_size = Math.sqrt(sigmaQ);
            }
            Double bottom = Math.sqrt(q.length)*Math.sqrt(sigmad2);
            RankTuple rt = new RankTuple(doc,(qdotd/(bottom * q_term_size)));
            result.add(rt);
        }
        return result;
    }

    private static PriorityQueue<RankTuple> calc_rank_ratio(String[] q, Comparator tc){
        PriorityQueue<RankTuple> result = new PriorityQueue<>(tc);
        if(q.length == 2){
            HashMap<Integer, Double> common = union(thedictionary.get(q[0]), thedictionary.get(q[1]));
            result.addAll(common.entrySet().stream().map(entry -> new RankTuple(entry.getKey(), Math.abs(entry.getValue()
                    - 2))).collect(Collectors.toList()));
        }
//        System.out.println(result);
        return result;

    }


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Breaks the main II Into num_of_slices tree maps to write them to a file
    private static void disk_handler() throws IOException {
        FileWriter fw = new FileWriter("/home/erfan/Documents/programming/invertedIndex/dataset/inverted_index/src/com/company/storage/ii" + 1 + ".xml");
        fw.write("<INVERTED_INDEX>\n");
        Integer num_of_slices = 1;
        TreeMap<String,LinkedList> treemap1 = new TreeMap<>();
        LinkedList<Tuple> templl;
        String[] kset = new String[thedictionary.size()];
        thedictionary.keySet().toArray(kset);
        for(String akey : kset){
            if(treemap1.size() < thedictionary.size()/num_of_slices){
                templl = thedictionary.get(akey);
                treemap1.put(akey,templl);
                thedictionary.remove(akey);
            }
            write_to_file(num_of_slices, treemap1,fw);
            treemap1.clear();
        }
        fw.write("</INVERTED_INDEX>\n");

    }

    // Gets a  tree map and attempts to write it to a file
    private static void write_to_file(Integer slices, TreeMap<String,LinkedList> tm,FileWriter fw) throws IOException {
        for(int slice = 1; slice <= slices ;slice++){
            String k;
            LinkedList<Tuple<Integer,Integer,ArrayList<Integer>>> templl;
            while(!tm.isEmpty()){
                k = tm.firstKey();
                fw.write("<KEY>\n");
                fw.write("<WORD>");
                fw.write(k);
                fw.write("</WORD>");

                fw.write("\n");
                templl = tm.get(k);
                fw.write("<POSTING_LIST>\n");
                for(Tuple<Integer, Integer,ArrayList<Integer>> t : templl){
                    fw.write("<DOC id=\"" + t.x.toString() + "\" tf=\"" + t.y.toString() + "\">\n");
                    fw.write("<POSITIONS>\n");
                    for(Integer position : t.z){
                        fw.write(position.toString() + " ");
                    }
                    fw.write("</POSITIONS>\n");
                    fw.write("</DOC>\n");
                }
                fw.write("</POSTING_LIST>\n");
                fw.write("/<KEY>\n");
                tm.remove(k);

            }

        }
    }

    @SuppressWarnings("unused")
    private void read_from_file(){
        try {

            File fXmlFile = new File("/home/erfan/IDE/IdeaProjects/inverted_index/src/com/company/storage/ll1.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fXmlFile);

            doc.getDocumentElement().normalize();

            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("KEY");

            System.out.println("----------------------------");

            for (int temp = 0; temp < nList.getLength(); temp++) {

                Node nNode = nList.item(temp);

                System.out.println("\nCurrent Element :" + nNode.getNodeName());

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element eElement = (Element) nNode;

                    String key = eElement.getElementsByTagName("WORD").item(0).getTextContent();
                    LinkedList<Tuple<Integer,Integer,ArrayList<Integer>>> templl = new LinkedList<>();
                    NodeList node_postinglist = doc.getElementsByTagName("POSTING_LIST");
                    for (int j = 0; j < nList.getLength(); j++) {
                        Node tpl = node_postinglist.item(j);
                        Element tpl1 = (Element) tpl;
                        Tuple<Integer, Integer, ArrayList<Integer>> t = new Tuple<>(Integer.parseInt(tpl1.getAttribute("id")),Integer.parseInt(tpl1.getAttribute("tf")),new ArrayList<Integer>());
                        templl.add(t);
                    }
                    thedictionary.put(key,templl);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /****************************************************HELPERS*******************************************************/

    private static HashMap<Integer, Double> intersect(LinkedList<Tuple<Integer, Integer, ArrayList<Integer>>> l1, LinkedList<Tuple<Integer, Integer, ArrayList<Integer>>> l2){
        HashMap<Integer, Double> resultList = new HashMap<>();
        int pIndex1 = 0;
        int pIndex2 = 0;
        Tuple<Integer, Integer, ArrayList<Integer>> pointer1 = l1.get(pIndex1);
        Tuple<Integer, Integer, ArrayList<Integer>> pointer2 = l2.get(pIndex2);
        boolean finished = false;
        while(!finished){
            if(pointer1.x.equals(pointer2.x)){
                System.out.println("doc: " + pointer1.x + " tf1: " + pointer1.y + " ft2: " + pointer2.y);
                resultList.put(pointer1.x, (pointer1.y * 1.0)/ pointer2.y);
                if(pIndex1 < l1.size()-1)
                    pointer1 = l1.get(++pIndex1);
                else
                    finished = true;
                if(pIndex2 < l2.size()-1)
                    pointer2 = l2.get(++pIndex2);
                else
                    finished = true;
            }
            else if(pointer1.x > pointer2.x) {
                if(pIndex1 < l1.size()-1)
                    pointer1 = l1.get(++pIndex1);
                else
                    finished = true;
            }
            else {
                if(pIndex2 < l2.size()-1)
                    pointer2 = l2.get(++pIndex2);
                else
                    finished = true;
            }
        }
//        System.out.println(resultList);
        return resultList;
    }

    @SuppressWarnings("Duplicates")
    private static HashMap<Integer, Double> union(LinkedList<Tuple<Integer, Integer, ArrayList<Integer>>> l1, LinkedList<Tuple<Integer, Integer, ArrayList<Integer>>> l2){
        HashMap<Integer, Double> resultList = new HashMap<>();
        int pIndex1 = 0;
        int pIndex2 = 0;
        Tuple<Integer, Integer, ArrayList<Integer>> pointer1 = l1.get(pIndex1);
        Tuple<Integer, Integer, ArrayList<Integer>> pointer2 = l2.get(pIndex2);
        boolean finished1 = false;
        boolean finished2 = false;
        while(!finished1 && !finished2){
            if(pointer1.x.equals(pointer2.x)){
                resultList.put(pointer1.x, ((pointer1.y * 1.0) + 0.9)/ (pointer2.y + 0.9));
                if(pIndex1 < l1.size()-1)
                    pointer1 = l1.get(++pIndex1);
                else
                    finished1 = true;
                if(pIndex2 < l2.size()-1)
                    pointer2 = l2.get(++pIndex2);
                else
                    finished2 = true;
            }
            else if(pointer1.x > pointer2.x) {
                if(pIndex1 < l1.size()-1) {
                    resultList.put(pointer1.x, (pointer1.y + 0.9) / 0.9);
                    pointer1 = l1.get(++pIndex1);
                } else
                    finished1 = true;
            }
            else {
                if(pIndex2 < l2.size()-1) {
                    resultList.put(pointer2.x, 0.9 / (pointer2.y + 0.9));
                    pointer2 = l2.get(++pIndex2);
                } else
                    finished2 = true;
            }
        }
        while (!finished1){
            if(pIndex1 < l1.size()-1) {
                resultList.put(pointer1.x, (pointer1.y + 0.9) / 0.9);
                pointer1 = l1.get(++pIndex1);
            } else
                finished1 = true;
        }
        while (!finished2){
            if(pIndex2 < l2.size()-1) {
                resultList.put(pointer2.x, 0.9 / (pointer2.y + 0.9));
                pointer2 = l2.get(++pIndex2);
            } else
                finished2 = true;
        }
        System.out.println(resultList);
        return resultList;
    }

}