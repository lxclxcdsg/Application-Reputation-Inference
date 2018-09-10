class Solution {
    class People implements Comparable<People>{
        int height;
        int rank;
        int originalIdx;
        People(int h, int k, int idx){
            height = h;
            rank = k;
            originalIdx = idx;
        }
        @Override
        public int compareTo(People that){
            if(this.rank != that.rank){
                return this.rank - that.rank;
            }
            return this.height - that.height;
        }
        
        @Override
        public String toString(){
            return height+" "+rank+" "+originalIdx;
        }
    }
    public int[][] reconstructQueue(int[][] people) {
        if(people == null || people.length == 0){
            return new int[0][0];
        }
        
        int[][] newQueue = new int[people.length][people[0].length];
        PriorityQueue<People> pq = new PriorityQueue<>();

        addPeople(pq, people);
        int idx = 0;
        while(!pq.isEmpty()){
            while(!pq.isEmpty() && pq.peek().rank == 0){
                People cur  = pq.poll();
                int curMin = cur.height;
                newQueue[idx][0] = people[cur.originalIdx][0];
                newQueue[idx][1] = people[cur.originalIdx][1];
                idx++;
                pq = updatePQ(pq, curMin);
                //System.out.println(pq);
            }
        }
        return newQueue;
    }
    
    private void addPeople(PriorityQueue<People> pq, int[][] people){
        for(int i=0; i< people.length; i++){
            People p = new People(people[i][0], people[i][1], i);
            pq.offer(p);
        }
    }
    
    private PriorityQueue<People> updatePQ(PriorityQueue<People> pq, int h){
        PriorityQueue<People> newQueue = new PriorityQueue<>();
        
        while(!pq.isEmpty()){
            People cur = pq.poll();
            if(cur.height <= h){
                cur.rank -= 1;
            }
            newQueue.offer(cur);
        }
        return newQueue;
    }
    
}