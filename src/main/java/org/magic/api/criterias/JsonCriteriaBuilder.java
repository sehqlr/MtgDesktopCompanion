package org.magic.api.criterias;
import static com.jayway.jsonpath.Criteria.where;
import static com.jayway.jsonpath.Filter.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.jayway.jsonpath.Filter;
import com.jayway.jsonpath.Predicate;


public class JsonCriteriaBuilder extends AbstractQueryBuilder<Filter> {
	
	
	public Filter build(MTGCrit<?>... crits) {
		
		List<Predicate> l = new ArrayList<>();
		
		for(MTGCrit<?> c : crits)
		{
			
			switch(c.getOperator())
			{
				case EQ: l.add(where(c.getAtt()).eq(c.getFirst()));break;
				case GREATER:l.add(where(c.getAtt()).gt(c.getFirst()));break;
				case GREATER_EQ:l.add(where(c.getAtt()).gte(c.getFirst()));break;
				case LIKE:l.add(where(c.getAtt()).regex(Pattern.compile("/^.*"+c.getFirst()+".*$/i")));break;
				case LOWER:l.add(where(c.getAtt()).lt(c.getFirst()));break;
				case LOWER_EQ:l.add(where(c.getAtt()).lte(c.getFirst()));break;
				case START_WITH:break;
				case IN:l.add(where(c.getAtt()).in(c.getVal()));break;
				case END_WITH :break;
			}
		}
		return filter(l);
	}

}