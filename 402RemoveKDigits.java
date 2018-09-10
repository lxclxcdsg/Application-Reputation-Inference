class Solution {
    public String removeKdigits(String num, int k) {
        int length = num.length();
        char[] arr = num.toCharArray();
        StringBuilder sb = new StringBuilder();
        Map<String, List<String>> map = new HashMap<>();
        List<String> candidates = rebuild(arr, 0, length-k,0, sb, map);
        Collections.sort(candidates);
        return candidates.get(0);
    }
    
    private List<String> rebuild(char[] arr, int idx, int target, int cur, StringBuilder sb, Map<String,List<String>> map){
        if(idx == arr.length){
            List<String> res = new ArrayList<>();
            if(cur == target){
                String s = new String(sb.toString());
                res.add(s);
            }
            return res;
        }
        
        if(cur > target){
            return new ArrayList<String>();
        }
        
        String key = idx +" "+ cur +" "+ sb.toString();
        if(map.containsKey(key)){
            return map.get(key);
        }
        List<String> res = new ArrayList<>();
        sb.append(arr[idx]);
        res.addAll(rebuild(arr, idx+1, target, cur+1, sb, map));
        sb.deleteCharAt(sb.length()-1);
        res.addAll(rebuild(arr, idx+1, target, cur, sb, map));
        map.put(key, res);
        return res;
    }
}


class Solution {
    public String removeKdigits(String num, int k) {
        int digits = num.length() - k;
        char[] stk = new char[num.length()];
        int top = 0;
        for(int i=0; i< num.length(); i++){
            char c = num.charAt(i);
            while(top> 0 && stk[top-1]> c && k > 0){
                top -=1;
                k -= 1;
            }
            stk[top++] = c;
        }
        int idx = 0;
        while(idx < digits && stk[idx] == '0') idx++;
        return idx == digits? "0":new String(stk, idx, digits-idx);
    }
}