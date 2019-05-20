package com.github.chriweis.querydsl.util.tools;

import com.querydsl.core.Tuple;
import com.querydsl.sql.RelationalPathBase;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ExtractedTuple {

    private RelationalPathBase<?> relationalPath;
    private Tuple tuple;
}
